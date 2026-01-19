package com.example.kalanacommerce.presentation.screen.dashboard.history

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.kalanacommerce.R
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import com.example.kalanacommerce.domain.model.Order
import com.example.kalanacommerce.domain.model.OrderStatus
import com.example.kalanacommerce.presentation.screen.dashboard.detail.product.glossyEffect
import org.koin.androidx.compose.koinViewModel
import java.util.Locale
import kotlin.math.abs

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: OrderHistoryViewModel = koinViewModel(),
    themeSetting: ThemeSetting,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToGroupDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchOrders()
    }

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

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Background Image
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            if (isDarkActive) Color.Black.copy(0.3f) else Color.White.copy(0.3f),
                            Color.Transparent,
                            if (isDarkActive) Color.Black.copy(0.1f) else Color.White.copy(0.1f)
                        )
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0.dp),
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // --- Header Section ---
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.history_title),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = mainGreen
                    )
                    Text(
                        text = "Riwayat pesanan kamu",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDarkActive) Color.White.copy(0.7f) else Color.Gray
                    )
                }

                // --- Tabs ---
                val tabs = listOf(
                    stringResource(R.string.history_tab_process),
                    stringResource(R.string.history_tab_completed),
                    stringResource(R.string.history_tab_cancelled)
                )

                TabRow(
                    selectedTabIndex = uiState.selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = mainGreen,
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[uiState.selectedTab])
                                .height(4.dp)
                                .padding(horizontal = 20.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(mainGreen)
                        )
                    },
                    divider = { },
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = uiState.selectedTab == index,
                            onClick = { viewModel.onTabSelected(index) },
                            text = {
                                Text(
                                    text = title,
                                    color = if (uiState.selectedTab == index) mainGreen else if (isDarkActive) Color.Gray else Color.DarkGray,
                                    fontWeight = if (uiState.selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                // --- List Content ---
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = mainGreen)
                    }
                } else if (uiState.historyItems.isEmpty()) {
                    EmptyState(isDarkActive)
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp, start = 20.dp, end = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.historyItems) { item ->
                            when (item) {
                                is HistoryUiItem.Single -> {
                                    OrderCardItemRefined(
                                        order = item.order,
                                        isDark = isDarkActive,
                                        onClick = { onNavigateToDetail(item.order.id) }
                                    )
                                }
                                is HistoryUiItem.Group -> {
                                    GroupOrderCard(
                                        groupItem = item,
                                        isDark = isDarkActive,
                                        onDetailClick = onNavigateToDetail,
                                        onGroupClick = { groupId -> onNavigateToGroupDetail(groupId) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SINGLE ORDER CARD ---
@Composable
fun OrderCardItemRefined(
    order: Order,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val statusColor = getStatusColor(order.status)
    val statusText = getStatusText(order.status)
    val textColor = if (isDark) Color.White else Color.Black
    val subTextColor = if (isDark) Color.White.copy(0.6f) else Color.Gray

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyEffect(isDark, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            // Header: Icon Toko Biasa
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = "Store",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = order.outletName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = textColor
                        )
                        Text(
                            text = order.date,
                            fontSize = 10.sp,
                            color = subTextColor
                        )
                    }
                }

                StatusBadge(text = statusText, color = statusColor)
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.5.dp,
                color = if (isDark) Color.White.copy(0.1f) else Color.Black.copy(0.05f)
            )

            // Content Preview
            val firstItem = order.items.firstOrNull()
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Box Image Produk
                if (firstItem != null && firstItem.image.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) Color.White.copy(0.05f) else Color.Gray.copy(0.1f),
                        modifier = Modifier.size(60.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(firstItem.image)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    // Fallback jika tidak ada gambar produk -> Single Order -> HIJAU
                    StoreGraphicBox(
                        seedKey = order.outletName,
                        borderColor = Color(0xFF4CAF50), // [WARNA HIJAU UNTUK TUNGGAL]
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(60.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = firstItem?.productName ?: "Item Order",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (order.itemCount > 1) "+ ${order.itemCount - 1} produk lainnya" else "${firstItem?.quantity ?: 1} Barang",
                        fontSize = 12.sp,
                        color = subTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Total Belanja", fontSize = 10.sp, color = subTextColor)
                    Text(
                        text = String.format(Locale("id", "ID"), "Rp %,d", order.totalAmount),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Tombol Aksi
                if (order.status == OrderStatus.PENDING) {
                    OutlinedButton(
                        onClick = onClick,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Bayar", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                } else if (order.status == OrderStatus.COMPLETED) {
                    Button(
                        onClick = { /* Logic Beli Lagi */ },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text("Beli Lagi", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// --- GROUP ORDER CARD (MULTI STORE) ---
@Composable
fun GroupOrderCard(
    groupItem: HistoryUiItem.Group,
    isDark: Boolean,
    onDetailClick: (String) -> Unit,
    onGroupClick: (String) -> Unit
) {
    val firstOrder = groupItem.orders.first()
    val totalGroupAmount = groupItem.orders.sumOf { it.totalAmount }
    val itemCountTotal = groupItem.orders.sumOf { it.itemCount }

    val statusColor = getStatusColor(firstOrder.status)
    val statusText = if (firstOrder.status == OrderStatus.PENDING) "Menunggu Bayar" else "Transaksi Gabungan"
    val textColor = if (isDark) Color.White else Color.Black
    val subTextColor = if (isDark) Color.White.copy(0.6f) else Color.Gray

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyEffect(isDark, RoundedCornerShape(24.dp))
            .clickable { onGroupClick(groupItem.paymentGroupId) }
            .padding(16.dp)
    ) {
        Column {
            // Header Group: Icon Biasa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Paket ${groupItem.orders.size} Toko",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = textColor
                    )
                }
                StatusBadge(text = statusText, color = statusColor)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = subTextColor.copy(0.2f))

            // List Order Kecil (Per Toko)
            groupItem.orders.forEachIndexed { index, order ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDetailClick(order.id) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // [UPDATED] Gabungan Order -> OREN
                    StoreGraphicBox(
                        seedKey = order.outletName,
                        borderColor = Color(0xFFFF9800), // [WARNA OREN UNTUK GABUNGAN]
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(order.outletName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = textColor)
                        Text("Order #${order.orderCode.takeLast(6)}", fontSize = 11.sp, color = subTextColor)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = subTextColor)
                }
                if (index < groupItem.orders.size - 1) {
                    HorizontalDivider(thickness = 0.5.dp, color = subTextColor.copy(0.1f), modifier = Modifier.padding(start = 52.dp))
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = subTextColor.copy(0.2f))

            // Footer Group
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$itemCountTotal Item Total", fontSize = 12.sp, color = subTextColor)

                if (firstOrder.status == OrderStatus.PENDING) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = String.format(Locale("id", "ID"), "Total: Rp %,d", totalGroupAmount),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Button(
                            onClick = { onGroupClick(groupItem.paymentGroupId) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Bayar Semua", fontSize = 11.sp)
                        }
                    }
                } else {
                    Text(
                        text = String.format(Locale("id", "ID"), "Total: Rp %,d", totalGroupAmount),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = textColor
                    )
                }
            }
        }
    }
}

// --- UTILS & HELPERS ---

// [NEW COMPONENT REVISED]
// Kotak Grafis Toko:
// - Gambar Full (ContentScale.Crop, tanpa padding)
// - Warna ditentukan oleh parameter [borderColor]
@Composable
fun StoreGraphicBox(
    seedKey: String,
    borderColor: Color, // [PARAMETER BARU]
    shape: Shape,
    modifier: Modifier = Modifier
) {
    val randomSeed = abs(seedKey.hashCode())
    val storeIcons = listOf(
        R.drawable.ic_sayuran,
        R.drawable.ic_buah,
        R.drawable.ic_proteinhewani,
        R.drawable.ic_bahanpokok,
        R.drawable.ic_bumbu
    )
    val iconRes = storeIcons[randomSeed % storeIcons.size]

    Box(
        modifier = modifier
            .background(Color.White, shape) // Background putih bersih di belakang gambar
            .border(2.dp, borderColor, shape), // Outline sesuai parameter
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            contentScale = ContentScale.Crop, // FULL IMAGE
            modifier = Modifier
                .fillMaxSize()
                .clip(shape) // Clip agar sesuai bentuk kotak
        )
    }
}

@Composable
fun StatusBadge(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(0.2f))
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EmptyState(isDark: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingBag,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = if (isDark) Color.White.copy(0.05f) else Color.Black.copy(0.05f),
                    shape = CircleShape
                )
                .padding(20.dp),
            tint = if (isDark) Color.White.copy(0.5f) else Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.history_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = if (isDark) Color.White.copy(0.7f) else Color.Gray
        )
    }
}

fun getStatusColor(status: OrderStatus): Color {
    return when (status) {
        OrderStatus.PENDING -> Color(0xFFFF9800)
        OrderStatus.PAID, OrderStatus.PROCESSED, OrderStatus.SHIPPED -> Color(0xFF2196F3)
        OrderStatus.COMPLETED -> Color(0xFF4CAF50)
        OrderStatus.CANCELLED, OrderStatus.FAILED, OrderStatus.EXPIRED -> Color(0xFFF44336)
        else -> Color.Gray
    }
}

fun getStatusText(status: OrderStatus): String {
    return when (status) {
        OrderStatus.PENDING -> "Menunggu Bayar"
        OrderStatus.PAID -> "Dibayar"
        OrderStatus.PROCESSED -> "Diproses"
        OrderStatus.SHIPPED -> "Dikirim"
        OrderStatus.COMPLETED -> "Selesai"
        OrderStatus.CANCELLED -> "Dibatalkan"
        OrderStatus.FAILED -> "Gagal"
        OrderStatus.EXPIRED -> "Kadaluarsa"
        else -> "Unknown"
    }
}