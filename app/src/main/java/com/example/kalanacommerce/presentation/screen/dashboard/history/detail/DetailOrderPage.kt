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

@Composable
fun DetailOrderPage(
    orderId: String,
    viewModel: DetailOrderViewModel = koinViewModel(),
    themeSetting: ThemeSetting,
    onBackClick: () -> Unit
) {
    LaunchedEffect(orderId) {
        viewModel.loadOrderDetail(orderId)
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
    val contentColor = if (isDarkActive) Color.White else Color.Black
    val mainGreen = Color(0xFF43A047)

    BackHandler { onBackClick() }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Loading
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = mainGreen)
            }
        } else if (uiState.order != null) {
            val order = uiState.order!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header (Back + Title)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 32.dp, bottom = 24.dp)
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
                        text = stringResource(R.string.order_detail_title),
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

                Spacer(modifier = Modifier.height(32.dp))
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
        OrderStatus.CANCELLED -> Color(0xFFF44336)
        else -> Color.Gray
    }

    val statusLabel = when(order.status) {
        OrderStatus.PENDING -> "Menunggu Pembayaran"
        OrderStatus.PAID -> "Pembayaran Berhasil"
        OrderStatus.PROCESSED -> "Pesanan Diproses"
        OrderStatus.SHIPPED -> "Pesanan Dikirim"
        OrderStatus.COMPLETED -> "Pesanan Selesai"
        OrderStatus.CANCELLED -> "Pesanan Dibatalkan"
        else -> "Status Tidak Diketahui"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyEffect(isDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.order_status),
                style = MaterialTheme.typography.labelMedium,
                color = if (isDark) Color.White.copy(0.6f) else Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = statusLabel,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = statusColor
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
                    // Format date simpel, bisa pakai formatter kalo mau
                    Text(order.date.take(10), fontSize = 13.sp, fontWeight = FontWeight.Medium, color = if(isDark) Color.White else Color.Black)
                }
            }
        }
    }
}

@Composable
fun OrderItemsSection(items: List<OrderItem>, storeName: String, isDark: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyEffect(isDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            // Header Toko
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Store, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = storeName,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color.Black
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // List Produk
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gambar
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

                    // Info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.productName,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = if (isDark) Color.White else Color.Black,
                            maxLines = 1
                        )
                        if (item.variantName != "-" && item.variantName.isNotEmpty()) {
                            Text(
                                text = "Varian: ${item.variantName}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = "${item.quantity} x ${String.format(Locale("id", "ID"), "Rp%,d", item.price)}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    // Total per item
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

            // Payment Method
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.order_payment_method), color = Color.Gray, fontSize = 13.sp)
                Text(order.paymentMethod, fontWeight = FontWeight.Medium, color = if(isDark) Color.White else Color.Black, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color.Gray.copy(0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            // Total
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringResource(R.string.order_total),
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color.Black
                )
                Text(
                    text = String.format(Locale("id", "ID"), "Rp %,d", order.totalAmount),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}