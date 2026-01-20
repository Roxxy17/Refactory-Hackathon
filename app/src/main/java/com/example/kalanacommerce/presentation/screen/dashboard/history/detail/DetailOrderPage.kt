package com.example.kalanacommerce.presentation.screen.dashboard.history.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

// Warna Brand (Sama seperti sebelumnya)
val BrandOrange = Color(0xFFF96D20)
val BrandGreen = Color(0xFF43A047)
val BrandRed = Color(0xFFE53935)
val BrandBlue = Color(0xFF2196F3)
val BrandYellow = Color(0xFFFFA000)

@Composable
fun DetailOrderPage(
    orderId: String,
    viewModel: DetailOrderViewModel = koinViewModel(),
    themeSetting: ThemeSetting,
    onBackClick: () -> Unit,
    onNavigateToPayment: (String, String, String?) -> Unit,
    // [UBAH] Parameter sekarang String (orderId), bukan Double (Lat/Long)
    onNavigateToMaps: (String) -> Unit
) {
    LaunchedEffect(orderId) {
        viewModel.loadOrderDetail(orderId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val systemInDark = isSystemInDarkTheme()
    val isDarkActive = remember(themeSetting, systemInDark) {
        when (themeSetting) {
            ThemeSetting.LIGHT -> false
            ThemeSetting.DARK -> true
            ThemeSetting.SYSTEM -> systemInDark
        }
    }
    val backgroundImage = if (isDarkActive) R.drawable.splash_background_black else R.drawable.splash_background_white
    val textColor = if (isDarkActive) Color.White else Color.Black
    val subTextColor = if (isDarkActive) Color.White.copy(0.6f) else Color.Gray

    BackHandler { onBackClick() }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            if (isDarkActive) Color.Black.copy(0.4f) else Color.White.copy(0.4f),
                            Color.Transparent
                        )
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0.dp),
            bottomBar = {
                val order = uiState.order
                if (order != null) {
                    // KONDISI 1: Belum Bayar -> Tombol Bayar
                    if (order.status == OrderStatus.PENDING) {
                        PaymentBottomBarEnhanced(
                            totalAmount = order.totalAmount,
                            isPending = true,
                            onPayClick = {
                                val paymentUrl = order.snapRedirectUrl
                                val targetUrl = if (!paymentUrl.isNullOrEmpty()) paymentUrl else order.snapToken
                                if (!targetUrl.isNullOrEmpty()) {
                                    onNavigateToPayment(targetUrl, orderId, order.paymentGroupId)
                                }
                            },
                            onMapsClick = {}
                        )
                    }
                    // KONDISI 2: Sudah Bayar & Belum Diambil -> Tombol Tracking (Ke OrderSuccess)
                    else if ((order.status == OrderStatus.PAID || order.status == OrderStatus.PROCESSED) && order.pickupStatus != "PICKED_UP") {
                        PaymentBottomBarEnhanced(
                            totalAmount = order.totalAmount,
                            isPending = false,
                            onPayClick = {},
                            onMapsClick = {
                                // [UBAH] Panggil navigasi dengan Order ID
                                onNavigateToMaps(orderId)
                            }
                        )
                    }
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
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.statusBarsPadding().padding(bottom = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .glossyEffect(isDarkActive, CircleShape)
                                .clickable { onBackClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = textColor
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Rincian Pesanan",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                    }

                    // Section Komponen (Sama seperti sebelumnya)
                    OrderTrackerSection(order, isDarkActive, textColor, subTextColor)
                    Spacer(modifier = Modifier.height(20.dp))
                    OrderIdSection(context, order, isDarkActive, textColor, subTextColor)
                    Spacer(modifier = Modifier.height(20.dp))
                    OrderItemsListEnhanced(order.items, order.outletName, isDarkActive, textColor, subTextColor)
                    Spacer(modifier = Modifier.height(20.dp))
                    OrderPaymentDetailEnhanced(order, isDarkActive, textColor, subTextColor)
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    }
}

// --- COMPONENTS ---

@Composable
fun OrderTrackerSection(
    order: Order,
    isDark: Boolean,
    textColor: Color,
    subTextColor: Color
) {
    // [LOGIC STATUS DINAMIS]
    val (statusColor, statusIcon, statusText, desc) = when {
        // 1. PENDING
        order.status == OrderStatus.PENDING -> Quad(
            BrandOrange,
            Icons.Default.Payment,
            "Menunggu Pembayaran",
            "Mohon selesaikan pembayaran sebelum batas waktu berakhir."
        )
        // 2. PAID / PROCESSED (Cek status pickup)
        order.status == OrderStatus.PAID || order.status == OrderStatus.PROCESSED -> {
            when (order.pickupStatus) {
                "READY" -> Quad(
                    BrandYellow, // Orange/Kuning Warning
                    Icons.Default.Store,
                    "Siap Diambil",
                    "Silakan datang ke toko dan tunjukkan kode pesanan ini."
                )
                "PICKED_UP" -> Quad(
                    BrandGreen,
                    Icons.Default.Check,
                    "Selesai Diambil",
                    "Barang telah berhasil diambil. Terima kasih!"
                )
                else -> Quad( // "PROCESS"
                    BrandBlue,
                    Icons.Default.Inventory2, // Ikon Paket/Barang
                    "Pesanan Diproses",
                    "Penjual sedang menyiapkan barang pesananmu."
                )
            }
        }
        // 3. COMPLETED
        order.status == OrderStatus.COMPLETED -> Quad(
            BrandGreen,
            Icons.Default.Check,
            "Pesanan Selesai",
            "Transaksi telah selesai sepenuhnya."
        )
        // 4. CANCELLED / FAILED
        else -> Quad(
            BrandRed,
            Icons.Default.ShoppingBag,
            "Dibatalkan",
            "Pesanan ini telah dibatalkan."
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyEffect(isDark, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = CircleShape,
                color = statusColor.copy(alpha = 0.1f),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = statusText,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = statusColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = subTextColor,
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// Helper Class untuk Return 4 Value
data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun OrderIdSection(
    context: Context,
    order: Order,
    isDark: Boolean,
    textColor: Color,
    subTextColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyEffect(isDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ID Pesanan",
                    style = MaterialTheme.typography.labelMedium,
                    color = subTextColor
                )
                Text(
                    text = order.orderCode,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = textColor
                )
            }

            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Order ID", order.orderCode)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "ID Pesanan disalin", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy ID",
                    tint = BrandGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun OrderItemsListEnhanced(
    items: List<OrderItem>,
    storeName: String,
    isDark: Boolean,
    textColor: Color,
    subTextColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyEffect(isDark, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Store, null, tint = BrandGreen, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = storeName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textColor
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = if(isDark) Color.White.copy(0.1f) else Color.Black.copy(0.05f)
            )

            items.forEachIndexed { index, item ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if(isDark) Color.White.copy(0.05f) else Color.Gray.copy(0.1f),
                        modifier = Modifier.size(64.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.image)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.productName,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = textColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (item.variantName != "-" && item.variantName.isNotEmpty()) {
                            Text(
                                text = "Varian: ${item.variantName}",
                                fontSize = 12.sp,
                                color = subTextColor
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${item.quantity} x ${String.format(Locale("id", "ID"), "Rp%,d", item.price)}",
                            fontSize = 12.sp,
                            color = subTextColor
                        )
                    }

                    Text(
                        text = String.format(Locale("id", "ID"), "Rp%,d", item.totalPrice),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = textColor
                    )
                }

                if (index < items.size - 1) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun OrderPaymentDetailEnhanced(
    order: Order,
    isDark: Boolean,
    textColor: Color,
    subTextColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyEffect(isDark, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "Rincian Pembayaran",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Metode", color = subTextColor, fontSize = 14.sp)
                Text(order.paymentMethod, fontWeight = FontWeight.Medium, color = textColor, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Total Harga (${order.itemCount} barang)", color = subTextColor, fontSize = 14.sp)
                Text(String.format(Locale("id", "ID"), "Rp %,d", order.totalAmount), color = textColor, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Biaya Pengiriman", color = subTextColor, fontSize = 14.sp)
                Text("Rp 0", color = BrandGreen, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = if(isDark) Color.White.copy(0.2f) else Color.Black.copy(0.1f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Total Belanja", fontWeight = FontWeight.Bold, color = textColor, fontSize = 16.sp)
                Text(
                    text = String.format(Locale("id", "ID"), "Rp %,d", order.totalAmount),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = BrandGreen
                )
            }
        }
    }
}

// [FIX] Bottom Bar fleksibel (Bisa mode bayar atau mode rute)
@Composable
fun PaymentBottomBarEnhanced(
    totalAmount: Long,
    isPending: Boolean,
    onPayClick: () -> Unit,
    onMapsClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 24.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            // Baris Info Harga (Hanya jika pending atau mau ditampilkan selalu)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total Tagihan",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Text(
                    text = String.format(Locale("id", "ID"), "Rp %,d", totalAmount),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = BrandOrange
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Baris Tombol Aksi
            if (isPending) {
                // MODE BAYAR: Ada tombol "Lokasi" (disabled/info) dan "Bayar Sekarang"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tombol Lokasi (Bisa di-disable atau arah ke toko, tapi fokus bayar dulu)
                    OutlinedButton(
                        onClick = onMapsClick,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandBlue),
                        modifier = Modifier.weight(1f).height(48.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Map, null, tint = BrandBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Rute Toko", fontWeight = FontWeight.Bold, color = BrandBlue, fontSize = 13.sp)
                    }

                    Button(
                        onClick = onPayClick,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange, contentColor = Color.White),
                        modifier = Modifier.weight(1.5f).height(48.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text("Bayar Sekarang", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // MODE PICKUP: Hanya tombol "Lihat Rute / Ambil Barang"
                Button(
                    onClick = onMapsClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Default.Map, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lihat Rute / Ambil Barang", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}