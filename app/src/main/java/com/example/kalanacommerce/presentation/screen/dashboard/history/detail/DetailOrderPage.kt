package com.example.kalanacommerce.presentation.screen.dashboard.history.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.kalanacommerce.R
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import com.example.kalanacommerce.domain.model.Order
import com.example.kalanacommerce.domain.model.OrderItem
import com.example.kalanacommerce.domain.model.OrderStatus
import com.example.kalanacommerce.presentation.screen.dashboard.detail.product.glossyEffect
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

// Warna Brand
val BrandOrange = Color(0xFFF96D20)
val BrandGreen = Color(0xFF43A047)

@Composable
fun DetailOrderPage(
    orderId: String,
    viewModel: DetailOrderViewModel = koinViewModel(),
    themeSetting: ThemeSetting,
    onBackClick: () -> Unit,
    onNavigateToPayment: (String, String) -> Unit // [BARU] Callback ke Midtrans
) {
    LaunchedEffect(orderId) {
        viewModel.loadOrderDetail(orderId)
    }

    val uiState by viewModel.uiState.collectAsState()

    val systemInDark = isSystemInDarkTheme()
    val isDarkActive = remember(themeSetting, systemInDark) {
        when (themeSetting) {
            ThemeSetting.LIGHT -> false
            ThemeSetting.DARK -> true
            ThemeSetting.SYSTEM -> systemInDark
        }
    }
    val backgroundImage = if (isDarkActive) R.drawable.splash_background_black else R.drawable.splash_background_white
    val contentColor = if (isDarkActive) Color.White else Color.Black

    BackHandler { onBackClick() }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            // [BARU] Bottom Bar Khusus untuk "Bayar Sekarang"
            bottomBar = {
                val order = uiState.order
                // Hanya muncul jika status PENDING (Menunggu Pembayaran)
                if (order != null && order.status == OrderStatus.PENDING) {
                    PaymentBottomBar(
                        totalAmount = order.totalAmount,
                        onPayClick = {
                            val snapToken = order.snapToken
                            // [PERBAIKAN 2] Kirim snapToken DAN orderId
                            if (!snapToken.isNullOrEmpty()) {
                                onNavigateToPayment(snapToken, orderId)
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandGreen)
                }
            } else if (uiState.order != null) {
                val order = uiState.order!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Header (Back + Title)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .glossyEffect(isDarkActive, CircleShape)
                                .clickable { onBackClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = contentColor
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Detail Pesanan",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = contentColor
                        )
                    }

                    // Status Card
                    OrderStatusCard(order, isDarkActive)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Items List
                    OrderItemsSection(order.items, order.outletName, isDarkActive)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Payment Info
                    OrderPaymentSection(order, isDarkActive)

                    Spacer(modifier = Modifier.height(100.dp)) // Spacer bawah agar tidak tertutup bottom bar
                }
            }
        }
    }
}

// [BARU] Bottom Bar Pembayaran
@Composable
fun PaymentBottomBar(
    totalAmount: Long,
    onPayClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Total Tagihan", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(
                    text = String.format(Locale("id", "ID"), "Rp %,d", totalAmount),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = BrandOrange
                )
            }

            Button(
                onClick = onPayClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                modifier = Modifier.height(48.dp)
            ) {
                Text("Bayar Sekarang", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Payment, null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun OrderStatusCard(order: Order, isDark: Boolean) {
    val statusColor = when (order.status) {
        OrderStatus.PENDING -> Color(0xFFFF9800)
        OrderStatus.PAID, OrderStatus.PROCESSED, OrderStatus.SHIPPED -> Color(0xFF2196F3)
        OrderStatus.COMPLETED -> Color(0xFF4CAF50)
        OrderStatus.CANCELLED, OrderStatus.FAILED, OrderStatus.EXPIRED -> Color(0xFFF44336)
        else -> Color.Gray
    }

    val statusLabel = when(order.status) {
        OrderStatus.PENDING -> "Menunggu Pembayaran"
        OrderStatus.PAID -> "Pembayaran Berhasil"
        OrderStatus.PROCESSED -> "Pesanan Diproses"
        OrderStatus.SHIPPED -> "Pesanan Dikirim"
        OrderStatus.COMPLETED -> "Pesanan Selesai"
        OrderStatus.CANCELLED -> "Dibatalkan"
        OrderStatus.FAILED -> "Pembayaran Gagal"
        OrderStatus.EXPIRED -> "Waktu Habis"
        else -> "Status Tidak Diketahui"
    }

    val statusDescription = when(order.status) {
        OrderStatus.PENDING -> "Selesaikan pembayaran agar pesanan diproses."
        OrderStatus.PAID -> "Terima kasih! Toko akan segera menyiapkan pesananmu."
        OrderStatus.PROCESSED -> "Pesananmu sedang disiapkan oleh penjual."
        OrderStatus.SHIPPED -> "Pesanan dalam pengiriman/siap diambil."
        OrderStatus.COMPLETED -> "Transaksi selesai. Terima kasih telah berbelanja."
        OrderStatus.CANCELLED -> "Pesanan ini telah dibatalkan."
        else -> "Hubungi bantuan jika ada kendala."
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyEffect(isDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_logo), // Placeholder
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusLabel,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = statusDescription,
                style = MaterialTheme.typography.bodySmall,
                color = if(isDark) Color.LightGray else Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.Gray.copy(0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(stringResource(R.string.order_id), fontSize = 11.sp, color = Color.Gray)
                    Text(order.orderCode, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = if(isDark) Color.White else Color.Black)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(stringResource(R.string.order_date), fontSize = 11.sp, color = Color.Gray)
                    Text(order.date.take(10), fontSize = 13.sp, fontWeight = FontWeight.Medium, color = if(isDark) Color.White else Color.Black)
                }
            }
        }
    }
}

// ... OrderItemsSection dan OrderPaymentSection TETAP SAMA (tidak perlu diubah) ...
// Sertakan kode OrderItemsSection dan OrderPaymentSection dari file sebelumnya di bawah sini.
@Composable
fun OrderItemsSection(items: List<OrderItem>, storeName: String, isDark: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyEffect(isDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Store, null, tint = BrandGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = storeName,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color.Black
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(60.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current).data(item.image).crossfade(true).build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.productName,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = if (isDark) Color.White else Color.Black,
                            maxLines = 1
                        )
                        if (item.variantName != "-" && item.variantName.isNotEmpty()) {
                            Text(text = "Varian: ${item.variantName}", fontSize = 12.sp, color = Color.Gray)
                        }
                        Text(
                            text = "${item.quantity} x ${String.format(Locale("id", "ID"), "Rp%,d", item.price)}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Text(
                        text = String.format(Locale("id", "ID"), "Rp%,d", item.totalPrice),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isDark) Color.White else Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun OrderPaymentSection(order: Order, isDark: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyEffect(isDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.order_payment_summary),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isDark) Color.White else Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.order_payment_method), color = Color.Gray, fontSize = 13.sp)
                Text(order.paymentMethod, fontWeight = FontWeight.Medium, color = if(isDark) Color.White else Color.Black, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color.Gray.copy(0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringResource(R.string.order_total),
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color.Black
                )
                Text(
                    text = String.format(Locale("id", "ID"), "Rp %,d", order.totalAmount),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = BrandGreen
                )
            }
        }
    }
}