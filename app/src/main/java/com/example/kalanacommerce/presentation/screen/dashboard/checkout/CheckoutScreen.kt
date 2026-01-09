package com.example.kalanacommerce.presentation.screen.dashboard.checkout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kalanacommerce.R
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import com.example.kalanacommerce.domain.model.CartItem
import com.example.kalanacommerce.presentation.screen.dashboard.product.glossyContainer
import com.example.kalanacommerce.presentation.screen.dashboard.product.glossyEffect
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun CheckoutScreen(
    itemIdsString: String, // ID yang dikirim dari Cart (comma separated)
    viewModel: CheckoutViewModel = koinViewModel(),
    themeSetting: ThemeSetting,
    onBackClick: () -> Unit,
    onNavigateToPayment: (String) -> Unit // Mengirim snapToken ke Payment Screen/Webview
) {
    // Parse ID saat pertama kali dibuka
    LaunchedEffect(itemIdsString) {
        val ids = itemIdsString.split(",").filter { it.isNotEmpty() }
        viewModel.loadCheckoutItems(ids)
    }

    val uiState by viewModel.uiState.collectAsState()

    // --- Setup Tema ---
    val systemInDark = isSystemInDarkTheme()
    val isDarkActive = remember(themeSetting, systemInDark) {
        when (themeSetting) {
            ThemeSetting.LIGHT -> false
            ThemeSetting.DARK -> true
            ThemeSetting.SYSTEM -> systemInDark
        }
    }
    val backgroundImage = if (isDarkActive) R.drawable.splash_background_black else R.drawable.splash_background_white
    val mainGreen = Color(0xFF43A047)
    val contentColor = if (isDarkActive) Color.White else Color.Black

    // Navigasi jika sukses dapat Snap Token
    LaunchedEffect(uiState.checkoutResult) {
        uiState.checkoutResult?.let { result ->
            onNavigateToPayment(result.snapToken)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // Header
            CheckoutHeader(onBackClick, isDarkActive)

            if (uiState.isLoading && uiState.checkoutItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = mainGreen)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // 1. Alamat Pengiriman
                    item {
                        SectionTitle("Alamat Pengiriman", contentColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        AddressCard(
                            address = uiState.selectedAddress,
                            isDark = isDarkActive,
                            onChangeClick = { /* Navigasi ke List Alamat */ }
                        )
                    }

                    // 2. Daftar Barang
                    item {
                        SectionTitle("Daftar Pesanan", contentColor)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(uiState.checkoutItems) { item ->
                        CheckoutItemCard(item, isDarkActive, mainGreen)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // 3. Rincian Pembayaran
                    item {
                        SectionTitle("Rincian Pembayaran", contentColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        PaymentSummaryCard(
                            subtotal = uiState.subtotal,
                            shipping = uiState.shippingCost,
                            service = uiState.serviceFee,
                            total = uiState.totalPayment,
                            isDark = isDarkActive,
                            mainColor = mainGreen
                        )
                        // Spacer bawah untuk bottom bar
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }

        // Bottom Bar (Bayar)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            PayBottomBar(
                total = uiState.totalPayment,
                isLoading = uiState.isLoading,
                isDark = isDarkActive,
                mainColor = mainGreen,
                onPayClick = { viewModel.placeOrder() }
            )
        }
    }
}

// --- COMPONENTS ---

@Composable
fun CheckoutHeader(onBack: () -> Unit, isDark: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, bottom = 16.dp, start = 24.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .glossyEffect(isDark, CircleShape)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = if (isDark) Color.White else Color.Black
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Pengiriman", // Atau Checkout
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = if (isDark) Color.White else Color.Black
        )
    }
}

@Composable
fun SectionTitle(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = color
    )
}

@Composable
fun AddressCard(address: AddressUiModel?, isDark: Boolean, onChangeClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyContainer(isDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        if (address != null) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Alamat Utama",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Ubah",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if(isDark) Color.White.copy(0.7f) else Color.Gray,
                        modifier = Modifier.clickable { onChangeClick() }
                    )
                }
                Divider(color = Color.Gray.copy(0.2f), modifier = Modifier.padding(vertical = 12.dp))
                Text(
                    text = "${address.name} (${address.phone})",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = if(isDark) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = address.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = if(isDark) Color.White.copy(0.7f) else Color.Gray,
                    lineHeight = 18.sp
                )
            }
        } else {
            // State jika belum ada alamat
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onChangeClick() }
            ) {
                Text("Pilih Alamat Pengiriman", color = if(isDark) Color.White else Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.rotate(180f))
            }
        }
    }
}

@Composable
fun CheckoutItemCard(item: CartItem, isDark: Boolean, mainColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyContainer(isDark, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Image
            Card(shape = RoundedCornerShape(8.dp), modifier = Modifier.size(60.dp)) {
                AsyncImage(
                    model = item.productImage, contentDescription = null,
                    contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = if(isDark) Color.White else Color.Black,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                if (item.variantName != "-") {
                    Text(text = item.variantName, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${item.quantity} x ${String.format(Locale("id", "ID"), "Rp%,d", item.price)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = String.format(Locale("id", "ID"), "Rp%,d", item.price * item.quantity),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = mainColor
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentSummaryCard(
    subtotal: Long,
    shipping: Long,
    service: Long,
    total: Long,
    isDark: Boolean,
    mainColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyContainer(isDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryRow("Subtotal Produk", subtotal, isDark)
            SummaryRow("Biaya Pengiriman", shipping, isDark)
            SummaryRow("Biaya Layanan", service, isDark)
            Divider(color = Color.Gray.copy(0.2f), modifier = Modifier.padding(vertical = 4.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Total Pembayaran", fontWeight = FontWeight.Bold, color = if(isDark) Color.White else Color.Black)
                Text(
                    text = String.format(Locale("id", "ID"), "Rp%,d", total),
                    fontWeight = FontWeight.Bold,
                    color = mainColor
                )
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: Long, isDark: Boolean) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = if(isDark) Color.White.copy(0.7f) else Color.Gray)
        Text(
            text = String.format(Locale("id", "ID"), "Rp%,d", value),
            style = MaterialTheme.typography.bodySmall,
            color = if(isDark) Color.White else Color.Black
        )
    }
}

@Composable
fun PayBottomBar(
    total: Long,
    isLoading: Boolean,
    isDark: Boolean,
    mainColor: Color,
    onPayClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyContainer(isDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Total Tagihan", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(
                    text = String.format(Locale("id", "ID"), "Rp%,d", total),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = mainColor
                )
            }

            Button(
                onClick = onPayClick,
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mainColor),
                modifier = Modifier.height(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Bayar Sekarang", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Outlined.ReceiptLong, null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}