package com.example.kalanacommerce.presentation.screen.dashboard.history.group

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionGroupScreen(
    paymentGroupId: String,
    viewModel: TransactionGroupViewModel = koinViewModel(),
    themeSetting: ThemeSetting,
    onBackClick: () -> Unit,
    onNavigateToOrderDetail: (String) -> Unit,
    onNavigateToMaps: (String) -> Unit
) {
    LaunchedEffect(paymentGroupId) {
        viewModel.loadGroupData(paymentGroupId)
    }

    val uiState by viewModel.uiState.collectAsState()

    // --- Setup Tema & Warna ---
    val systemInDark = isSystemInDarkTheme()
    val isDarkActive = remember(themeSetting, systemInDark) {
        when (themeSetting) {
            ThemeSetting.LIGHT -> false
            ThemeSetting.DARK -> true
            ThemeSetting.SYSTEM -> systemInDark
        }
    }

    // Asset & Color Palette
    val backgroundImage = if (isDarkActive) R.drawable.splash_background_black else R.drawable.splash_background_white
    val textColor = if (isDarkActive) Color.White else Color.Black
    val subTextColor = if (isDarkActive) Color.White.copy(0.6f) else Color.Gray
    val brandGreen = Color(0xFF43A047)

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background Image Full Screen
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Gradient Overlay agar konten terbaca
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            if (isDarkActive) Color.Black.copy(0.4f) else Color.White.copy(0.4f),
                            Color.Transparent,
                            if (isDarkActive) Color.Black.copy(0.2f) else Color.White.copy(0.2f)
                        )
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent, // Transparan agar background terlihat
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Rincian Gabungan",
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    },
                    navigationIcon = {
                        // Tombol Back Glossy
                        Box(
                            modifier = Modifier
                                .padding(start = 16.dp)
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
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = brandGreen)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 10.dp), // Margin kiri kanan
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. HEADER BRAND & ID (Glossy Box)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glossyEffect(isDarkActive, RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "KALANA",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = brandGreen,
                                letterSpacing = 2.sp
                            )

                            Column(horizontalAlignment = Alignment.End) {
                                val status = uiState.orders.firstOrNull()?.status ?: OrderStatus.UNKNOWN
                                StatusBadgeGroup(status)

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Group ID: #${paymentGroupId.takeLast(6).uppercase()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = subTextColor
                                )
                                Text(
                                    uiState.transactionDate,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = subTextColor
                                )
                            }
                        }
                    }

                    // 2. INFO TRANSAKSI (Glossy Box)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glossyEffect(isDarkActive, RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text("Info Transaksi", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Metode Pembayaran", color = subTextColor, fontSize = 13.sp)
                                Text(uiState.paymentMethod, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = textColor)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Total Toko", color = subTextColor, fontSize = 13.sp)
                                Text("${uiState.orders.size} Toko", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = textColor)
                            }
                        }
                    }

                    // 3. TITLE SECTION
                    Text(
                        "Daftar Pesanan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = textColor,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                    )

                    // 4. LOOPING TOKO (Glossy Box per Toko)
                    uiState.orders.forEach { order ->
                        StoreOrderCardTransparent(
                            order = order,
                            isDark = isDarkActive,
                            textColor = textColor,
                            subTextColor = subTextColor,
                            brandGreen = brandGreen,
                            onDetailClick = { onNavigateToOrderDetail(order.id) },
                            onMapsClick = { onNavigateToMaps(order.id) }
                        )
                    }

                    // 5. CARD RINCIAN BAYAR GABUNGAN (Glossy Box)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .glossyEffect(isDarkActive, RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text("Rincian Pembayaran", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Total Item (${uiState.totalItemCount})", color = subTextColor, fontSize = 13.sp)
                                Text(
                                    text = String.format(Locale("id", "ID"), "Rp %,d", uiState.totalGroupAmount),
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Biaya Layanan (Total)", color = subTextColor, fontSize = 13.sp)
                                Text("Rp 0", color = brandGreen, fontSize = 13.sp)
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = if (isDarkActive) Color.White.copy(0.2f) else Color.Black.copy(0.1f)
                            )

                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Grand Total", fontWeight = FontWeight.Bold, color = textColor)
                                Text(
                                    text = String.format(Locale("id", "ID"), "Rp %,d", uiState.totalGroupAmount),
                                    fontWeight = FontWeight.Bold,
                                    color = brandGreen,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

// --- COMPONENT PER TOKO (Transparent/Glossy) ---
@Composable
fun StoreOrderCardTransparent(
    order: Order,
    isDark: Boolean,
    textColor: Color,
    subTextColor: Color,
    brandGreen: Color,
    onDetailClick: () -> Unit,
    onMapsClick: () -> Unit // [BARU]
) {
    // [LOGIC STATUS LABEL]
    val pickupStatusText = when (order.pickupStatus) {
        "PROCESS" -> "Diproses"
        "READY" -> "Siap Diambil"
        "PICKED_UP" -> "Selesai"
        else -> ""
    }
    val pickupColor = when (order.pickupStatus) {
        "READY" -> Color(0xFFFFA000)
        "PROCESS" -> Color(0xFF2196F3)
        "PICKED_UP" -> Color(0xFF43A047)
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyEffect(isDark, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            // Header Toko
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDetailClick() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Store, null, tint = brandGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(order.outletName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = textColor)
                }

                // [BARU] Tampilkan Badge Status Pickup atau Tombol Rute
                if (order.status == OrderStatus.PAID && order.pickupStatus != "PICKED_UP") {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = pickupColor.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, pickupColor),
                        modifier = Modifier.clickable { onMapsClick() } // Klik badge -> Maps
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Map, null, tint = pickupColor, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(pickupStatusText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = pickupColor)
                        }
                    }
                } else {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = subTextColor, modifier = Modifier.size(16.dp))
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = if (isDark) Color.White.copy(0.1f) else Color.Black.copy(0.05f)
            )

            // Items Preview
            order.items.take(2).forEachIndexed { index, item ->
                GroupProductItemTransparent(item, isDark, textColor, subTextColor)
                if (index < order.items.size - 1 && index < 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (order.items.size > 2) {
                Text("+ ${order.items.size - 2} produk lainnya...", fontSize = 11.sp, color = subTextColor, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer (Subtotal + Tombol Aksi jika perlu)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                // Jika belum selesai, beri opsi Lihat Rute
                if ((order.status == OrderStatus.PAID || order.status == OrderStatus.PROCESSED) && order.pickupStatus != "PICKED_UP") {
                    Text(
                        text = "Lihat Rute",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3),
                        modifier = Modifier.clickable { onMapsClick() }
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Text(
                    text = "Subtotal: ${String.format(Locale("id", "ID"), "Rp %,d", order.totalAmount)}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun GroupProductItemTransparent(
    item: OrderItem,
    isDark: Boolean,
    textColor: Color,
    subTextColor: Color
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Image
        Surface(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.size(60.dp),
            color = if (isDark) Color.White.copy(0.05f) else Color.Black.copy(0.05f)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(item.image).crossfade(true).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Detail
        Column(modifier = Modifier.weight(1f)) {
            Text(item.productName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1, color = textColor)

            Text(
                text = "${item.quantity} x ${String.format(Locale("id", "ID"), "Rp %,d", item.price)}",
                color = subTextColor,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun StatusBadgeGroup(status: OrderStatus) {
    val (color, text) = when(status) {
        OrderStatus.PAID -> Color(0xFF00C853) to "PAID"
        OrderStatus.PENDING -> Color(0xFFFF9800) to "UNPAID"
        OrderStatus.CANCELLED -> Color(0xFFEF4444) to "CANCELLED"
        OrderStatus.COMPLETED -> Color(0xFF00C853) to "COMPLETE"
        else -> Color.Gray to status.name
    }

    // Transparent Badge
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f), // Sedikit transparan
        border = BorderStroke(1.dp, color.copy(0.3f)),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}