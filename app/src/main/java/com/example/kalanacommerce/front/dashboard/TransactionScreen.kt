package com.example.kalanacommerce.front.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.kalanacommerce.R

// Warna utama aplikasi
val PrimaryGreen = Color(0xFF069C6F)
val SecondaryOrange = Color(0xFFFF6B00)
val BackgroundLight = Color(0xFFF7F8FA)
val TextGray = Color(0xFF707070)

// --- Data Model ---
data class CartItem(
    val id: Int,
    val name: String,
    val price: Int,
    val oldPrice: Int,
    val discount: String,
    val imageRes: Int,
    var quantity: Int
)

// Dummy data
private fun getDummyCartItems(): List<CartItem> = listOf(
    CartItem(1, "Selada Air", 4000, 6000, "10%", R.drawable.seladaair, 1),
    CartItem(2, "Bayam Hijau", 5000, 7000, "15%", R.drawable.seladaair, 2),
    CartItem(3, "Pakcoy", 3500, 5000, "5%", R.drawable.seladaair, 1)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    onBack: () -> Unit = {},
    navController: NavController
) {
    val cartItems = remember { mutableStateListOf(*getDummyCartItems().toTypedArray()) }

    val totalItems = cartItems.sumOf { it.quantity }
    val subtotal = cartItems.sumOf { it.price * it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Transaksi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            CheckoutButton(totalItems = totalItems, totalPrice = subtotal)
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {
                Text(
                    text = "Keranjang Belanja",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                )
            }

            // Item list
            items(cartItems, key = { it.id }) { item ->
                CartItemCard(
                    item = item,
                    onQuantityChange = { newItem ->
                        val index = cartItems.indexOfFirst { it.id == newItem.id }
                        if (index != -1) {
                            if (newItem.quantity > 0) cartItems[index] = newItem
                            else cartItems.removeAt(index)
                        }
                    }
                )
            }

            // Voucher dan Detail Pembayaran
            item {
                Spacer(Modifier.height(8.dp))
                VoucherSection(vouchers = 2)
                Spacer(Modifier.height(16.dp))
                PaymentDetailSection(subtotal = subtotal, totalItems = totalItems)
            }
        }
    }
}

// --- Item Produk ---
@Composable
fun CartItemCard(item: CartItem, onQuantityChange: (CartItem) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(70.dp)) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = item.discount,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(
                            SecondaryOrange,
                            shape = RoundedCornerShape(bottomStart = 8.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                Spacer(Modifier.height(2.dp))
                Text(
                    "Rp ${"%,d".format(item.price)}",
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    "Rp ${"%,d".format(item.oldPrice)}",
                    color = TextGray.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    textDecoration = TextDecoration.LineThrough
                )
            }

            // PERUBAHAN UTAMA ADA PADA FUNGSI INI
            QuantityController(
                quantity = item.quantity,
                onIncrease = { onQuantityChange(item.copy(quantity = item.quantity + 1)) },
                onDecrease = { onQuantityChange(item.copy(quantity = item.quantity - 1)) }
            )
        }
    }
}

// --- Kontrol Kuantitas ---
@Composable
fun QuantityController(quantity: Int, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp) // Beri sedikit jarak lebih
    ) {
        // --- Tombol Kurang (-) ---
        // Menggunakan Box dan Icon, bukan IconButton
        Box(
            modifier = Modifier
                .size(26.dp) // Ukuran lebih kecil
                .clip(CircleShape)
                .background(if (quantity > 0) PrimaryGreen.copy(alpha = 0.1f) else Color(0xFFEEEEEE))
                .clickable(enabled = quantity > 0, onClick = onDecrease), // Logika di sini
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Remove,
                contentDescription = "Kurangi",
                tint = if (quantity > 0) PrimaryGreen else Color.LightGray,
                modifier = Modifier.size(14.dp) // Ikon lebih kecil
            )
        }

        Text(
            quantity.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )

        // --- Tombol Tambah (+) ---
        Box(
            modifier = Modifier
                .size(26.dp) // Ukuran lebih kecil
                .clip(CircleShape)
                .background(PrimaryGreen)
                .clickable(onClick = onIncrease),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Tambah",
                tint = Color.White,
                modifier = Modifier.size(14.dp) // Ikon lebih kecil
            )
        }
    }
}
// --- Voucher ---
@Composable
fun VoucherSection(vouchers: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(14.dp))
            .clickable { /* TODO: Navigasi ke voucher */ }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            buildAnnotatedString {
                append("Gunakan Voucher ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = PrimaryGreen)) {
                    append("($vouchers)")
                }
            },
            fontWeight = FontWeight.Medium
        )
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}

// --- Detail Pembayaran ---
@Composable
fun PaymentDetailSection(subtotal: Int, totalItems: Int) {
    val shippingCost = 10000
    val discount = 5000
    val total = subtotal + shippingCost - discount

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Detail Pembayaran",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            DetailRow("Subtotal ($totalItems item)", subtotal)
            DetailRow("Biaya Pengiriman", shippingCost)
            DetailRow("Diskon", -discount, color = SecondaryOrange)

            Divider(Modifier.padding(vertical = 8.dp), color = Color(0xFFE0E0E0))

            DetailRow("Total Pembayaran", total, color = PrimaryGreen, bold = true)
        }
    }
}

@Composable
fun DetailRow(label: String, amount: Int, color: Color = Color.Black, bold: Boolean = false) {
    val formatted = "Rp ${"%,d".format(kotlin.math.abs(amount))}"
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextGray, fontSize = 14.sp)
        Text(
            text = if (amount < 0) "- $formatted" else formatted,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
            fontSize = if (bold) 17.sp else 14.sp,
            color = color
        )
    }
}

// --- Tombol Checkout ---
@Composable
fun CheckoutButton(totalItems: Int, totalPrice: Int) {
    val buttonEnabled = totalItems > 0
    val background = if (buttonEnabled) PrimaryGreen else Color.LightGray

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("$totalItems Item", color = TextGray, fontSize = 13.sp)
                Text(
                    "Rp ${"%,d".format(totalPrice)}",
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryGreen,
                    fontSize = 18.sp
                )
            }

            Button(
                onClick = { /* TODO: Navigasi ke pembayaran */ },
                enabled = buttonEnabled,
                colors = ButtonDefaults.buttonColors(containerColor = background),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(50.dp)
            ) {
                Text("Bayar Sekarang", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun TransactionScreenPreview() {
    val navController = rememberNavController()
    TransactionScreen(navController = navController)
}
