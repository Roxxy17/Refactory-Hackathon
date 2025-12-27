//package com.example.kalanacommerce.ui.dashboar
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.FavoriteBorder
//import androidx.compose.material.icons.filled.Star
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextDecoration
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.kalanacommerce.R
//import com.example.kalanacommerce.ui.theme.KalanaCommerceTheme
//
//// Warna Putih Khusus dan Abu Terang (Disarankan untuk memisahkannya ke file tema)
//private val LightBackground = Color(0xFFF7F7F7)
//private val WhiteSurface = Color.White
//private val PrimaryGreen = Color(0xFF007F5F)
//private val SecondaryOrange = Color(0xFFFF6B00)
//
//// --- Data Model Dummy (Idealnya diimpor) ---
//data class ProductItem(
//    val id: Int,
//    val name: String,
//    val price: String,
//    val oldPrice: String,
//    val discount: String,
//    val imageRes: Int,
//    val tags: List<String> = emptyList()
//)
//
//val dummyProduct = ProductItem(
//    id = 10,
//    name = "Bawang Merah Segar Pilihan",
//    price = "Rp 12.000",
//    oldPrice = "Rp 15.000",
//    discount = "20%",
//    imageRes = R.drawable.bawang_merah, // Ganti dengan resource Anda
//    tags = listOf("promo", "bumbu")
//)
//// --- End Data Model Dummy ---
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProductDetailPage(
//    product: ProductItem = dummyProduct,
//    onBack: () -> Unit = {}
//) {
//    val colorScheme = MaterialTheme.colorScheme
//    val primaryColor = colorScheme.primary
//    val darkText = colorScheme.onSurface
//    var quantity by remember { mutableStateOf(1) }
//    var selectedTabIndex by remember { mutableStateOf(0) }
//    val tabs = listOf("Deskripsi", "Ulasan")
//
//    Scaffold(
//        bottomBar = {
//            DetailBottomBar(
//                primaryColor = primaryColor,
//                quantity = quantity,
//                onAddToCart = { /* Aksi Add to Cart */ }
//            )
//        },
//        containerColor = WhiteSurface
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .verticalScroll(rememberScrollState())
//        ) {
//            // --- 1. Header Gambar Produk ---
//            ProductImageHeader(product = product, onBack = onBack)
//
//            // --- 2. Detail Produk & Harga ---
//            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
//
//                // Nama Produk
//                Text(
//                    text = product.name,
//                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
//                    color = darkText
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                // Rating & Review Count
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Icon(Icons.Filled.Star, contentDescription = "Rating", tint = SecondaryOrange, modifier = Modifier.size(20.dp))
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text("4.8 (120 Ulasan)", color = Color.Gray, fontSize = 14.sp)
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Harga dan Counter Kuantitas
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    PriceSection(product = product, primaryColor = primaryColor)
//                    QuantityController(
//                        quantity = quantity,
//                        onIncrease = { quantity++ },
//                        onDecrease = { if (quantity > 1) quantity-- }
//                    )
//                }
//            }
//
//            Divider(modifier = Modifier.padding(vertical = 16.dp), color = LightBackground)
//
//            // --- 3. Tab Deskripsi & Ulasan ---
//            TabRow(
//                selectedTabIndex = selectedTabIndex,
//                modifier = Modifier.fillMaxWidth().background(WhiteSurface),
//                containerColor = WhiteSurface,
//                contentColor = primaryColor
//            ) {
//                tabs.forEachIndexed { index, title ->
//                    Tab(
//                        selected = selectedTabIndex == index,
//                        onClick = { selectedTabIndex = index },
//                        text = { Text(title, fontWeight = FontWeight.Bold) }
//                    )
//                }
//            }
//
//            // Konten Tab
//            Box(modifier = Modifier.padding(16.dp)) {
//                when (selectedTabIndex) {
//                    0 -> Text(
//                        text = "Bawang merah pilihan terbaik, ditanam secara organik dan dipanen saat matang sempurna. Cocok untuk semua masakan Nusantara. Berat bersih 250g per kemasan. Simpan di tempat sejuk dan kering.",
//                        color = Color.Gray,
//                        lineHeight = 22.sp
//                    )
//                    1 -> Text("Belum ada ulasan untuk produk ini.", color = Color.Gray)
//                }
//            }
//        }
//    }
//}
//
//// --- Komponen 1: Header Gambar dan Tombol Navigasi ---
//@Composable
//fun ProductImageHeader(product: ProductItem, onBack: () -> Unit) {
//    val darkText = MaterialTheme.colorScheme.onSurface
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(300.dp)
//            .background(LightBackground)
//    ) {
//        Image(
//            painter = painterResource(id = product.imageRes),
//            contentDescription = product.name,
//            modifier = Modifier.fillMaxSize(0.8f).align(Alignment.Center),
//            contentScale = ContentScale.Fit
//        )
//
//        // Tombol Back
//        IconButton(
//            onClick = onBack,
//            modifier = Modifier
//                .align(Alignment.TopStart)
//                .padding(16.dp)
//                .size(40.dp)
//                .clip(CircleShape)
//                .background(WhiteSurface)
//        ) {
//            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = darkText)
//        }
//
//        // Tombol Favorite
//        IconButton(
//            onClick = { /* Aksi Favorite */ },
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .padding(16.dp)
//                .size(40.dp)
//                .clip(CircleShape)
//                .background(WhiteSurface)
//        ) {
//            Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorit", tint = darkText)
//        }
//    }
//}
//
//// --- Komponen 2: Bagian Harga ---
//@Composable
//fun PriceSection(product: ProductItem, primaryColor: Color) {
//    Column {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Text(
//                text = product.price,
//                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
//                color = primaryColor
//            )
//            Spacer(modifier = Modifier.width(8.dp))
//            Text(
//                text = product.oldPrice,
//                style = MaterialTheme.typography.bodyLarge.copy(textDecoration = TextDecoration.LineThrough),
//                color = Color.Gray
//            )
//        }
//
//        // Label Diskon Kecil
//        Text(
//            text = "Hemat ${product.discount}",
//            color = SecondaryOrange,
//            fontWeight = FontWeight.SemiBold,
//            fontSize = 14.sp
//        )
//    }
//}
//
//// --- Komponen 3: Pengontrol Kuantitas (Disederhanakan) ---
//@Composable
//fun QuantityController(
//    quantity: Int,
//    onIncrease: () -> Unit,
//    onDecrease: () -> Unit
//) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .clip(RoundedCornerShape(8.dp))
//            .background(LightBackground)
//    ) {
//        // Tombol Kurang
//        IconButton(onClick = onDecrease, enabled = quantity > 1) {
//            Icon(Icons.Default.Remove, contentDescription = "Kurangi", tint = Color.Gray)
//        }
//
//        // Teks Kuantitas
//        Text(
//            text = quantity.toString(),
//            fontWeight = FontWeight.Bold,
//            color = MaterialTheme.colorScheme.onSurface,
//            modifier = Modifier.padding(horizontal = 8.dp)
//        )
//
//        // Tombol Tambah
//        IconButton(onClick = onIncrease) {
//            Icon(Icons.Default.Add, contentDescription = "Tambah", tint = PrimaryGreen)
//        }
//    }
//}
//
//// --- Komponen 4: Bottom Bar untuk Add to Cart ---
//@Composable
//fun DetailBottomBar(primaryColor: Color, quantity: Int, onAddToCart: () -> Unit) {
//    Surface(shadowElevation = 8.dp, color = WhiteSurface) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Tombol Chat (Opsional)
//            OutlinedButton(
//                onClick = { /* Aksi Chat */ },
//                modifier = Modifier.height(50.dp).width(120.dp),
//                shape = RoundedCornerShape(12.dp),
//                border = ButtonDefaults.outlinedButtonBorder.copy(color = primaryColor)
//            ) {
//                Text("Chat", color = primaryColor, fontWeight = FontWeight.Bold)
//            }
//
//            // Tombol Add to Cart
//            Button(
//                onClick = onAddToCart,
//                modifier = Modifier.height(50.dp).weight(1f).padding(start = 16.dp),
//                shape = RoundedCornerShape(12.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
//                contentPadding = PaddingValues(horizontal = 16.dp)
//            ) {
//                Text(
//                    text = "Tambah ke Keranjang",
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }
//    }
//}
//
//
//// --- PREVIEW ---
//@Preview(showBackground = true, device = "id:pixel_5")
//@Composable
//fun ProductDetailPagePreview() {
//    KalanaCommerceTheme {
//        ProductDetailPage(product = dummyProduct)
//    }
//}