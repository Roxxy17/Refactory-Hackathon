// File: presentation/screen/dashboard/history/group/TransactionGroupScreen.kt
package com.example.kalanacommerce.presentation.screen.dashboard.history.group

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ShoppingBag
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

// Warna Brand
val BrandOrange = Color(0xFFF96D20)
val BrandGreen = Color(0xFF43A047)
val BrandRed = Color(0xFFE53935)
val BrandBlue = Color(0xFF2196F3)
val BrandYellow = Color(0xFFFFA000)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionGroupScreen(
    paymentGroupId: String?, // Nullable
    viewModel: TransactionGroupViewModel = koinViewModel(),
    themeSetting: ThemeSetting,
    onBackClick: () -> Unit,
    onNavigateToOrderDetail: (String) -> Unit,
    onNavigateToMaps: (String?, String?) -> Unit
) {
    // [FIX] Gunakan safeGroupId ini di seluruh kode UI untuk menghindari error null
    val safeGroupId = paymentGroupId ?: ""

    LaunchedEffect(safeGroupId) {
        if (safeGroupId.isNotEmpty()) {
            viewModel.loadGroupData(safeGroupId)
        }
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
                            Color.Transparent,
                            if (isDarkActive) Color.Black.copy(0.2f) else Color.White.copy(0.2f)
                        )
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0.dp),
            bottomBar = {
                if (!uiState.isLoading && uiState.orders.isNotEmpty()) {
                    val firstOrder = uiState.orders.first()
                    val anyNotPickedUp = uiState.orders.any {
                        (it.status == OrderStatus.PAID || it.status == OrderStatus.PROCESSED) &&
                                it.pickupStatus != "PICKED_UP"
                    }

                    if (firstOrder.status == OrderStatus.PENDING) {
                        GroupPaymentBottomBar(
                            totalAmount = uiState.totalGroupAmount,
                            buttonText = "Bayar Semua (${uiState.orders.size} Toko)",
                            btnColor = BrandOrange,
                            isDark = isDarkActive,
                            onClick = {
                                val targetUrl = firstOrder.snapRedirectUrl ?: firstOrder.snapToken
                                if (!targetUrl.isNullOrEmpty()) {
                                    Toast.makeText(context, "Redirecting...", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    } else if (anyNotPickedUp) {
                        GroupPaymentBottomBar(
                            totalAmount = uiState.totalGroupAmount,
                            buttonText = "Lacak Semua Lokasi",
                            btnColor = BrandBlue,
                            isDark = isDarkActive,
                            onClick = { onNavigateToMaps(null, safeGroupId) },
                            icon = Icons.Default.Map
                        )
                    }
                }
            }
        ) { paddingValues ->
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandGreen)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    // --- Header Title ---
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
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = textColor)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Rincian Gabungan",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                    }

                    // --- 1. HEADER BRAND & ID ---
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
                                "KALANA",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = BrandGreen,
                                letterSpacing = 2.sp
                            )
                            Column(horizontalAlignment = Alignment.End) {
                                val status = uiState.orders.firstOrNull()?.status ?: OrderStatus.UNKNOWN
                                StatusBadgeGroup(status)
                                Spacer(modifier = Modifier.height(6.dp))

                                // [FIXED LINE] Gunakan safeGroupId, BUKAN paymentGroupId
                                Text(
                                    "Group ID: #${safeGroupId.takeLast(6).uppercase()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = subTextColor
                                )
                                Text(uiState.transactionDate, style = MaterialTheme.typography.labelSmall, color = subTextColor)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // --- 2. TRACKER SECTION ---
                    val masterStatus = uiState.orders.firstOrNull()?.status ?: OrderStatus.UNKNOWN
                    val overallPickupStatus = if (uiState.orders.any { it.pickupStatus == "READY" }) "READY"
                    else if (uiState.orders.any { it.pickupStatus == "PROCESS" }) "PROCESS"
                    else "PICKED_UP"

                    GroupTrackerSection(masterStatus, overallPickupStatus, isDarkActive, textColor, subTextColor)

                    Spacer(modifier = Modifier.height(20.dp))

                    // --- 3. GROUP INFO ---
                    GroupInfoSection(
                        context = context,
                        groupId = safeGroupId, // Pass String aman
                        date = uiState.transactionDate,
                        method = uiState.paymentMethod,
                        isDark = isDarkActive,
                        textColor = textColor,
                        subTextColor = subTextColor
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- 4. LIST TOKO HEADER ---
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Store, null, tint = BrandGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Daftar Pesanan Toko",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = textColor
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = if(isDarkActive) Color.White.copy(0.1f) else Color.Black.copy(0.05f)
                    )

                    // --- LIST TOKO CONTENT ---
                    uiState.orders.forEach { order ->
                        StoreOrderCardGlossy(
                            order = order,
                            isDark = isDarkActive,
                            textColor = textColor,
                            subTextColor = subTextColor,
                            brandGreen = BrandGreen,
                            onDetailClick = { onNavigateToOrderDetail(order.id) },
                            onMapsClick = { onNavigateToMaps(order.id, null) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- 5. RINCIAN PEMBAYARAN ---
                    GroupPaymentDetailSection(
                        totalAmount = uiState.totalGroupAmount,
                        itemCount = uiState.totalItemCount,
                        paymentMethod = uiState.paymentMethod,
                        isDark = isDarkActive,
                        textColor = textColor,
                        subTextColor = subTextColor
                    )

                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    }
}

// ==========================================
// COMPONENTS
// ==========================================

@Composable
fun GroupTrackerSection(
    status: OrderStatus,
    pickupStatus: String,
    isDark: Boolean,
    textColor: Color,
    subTextColor: Color
) {
    val (statusColor, statusIcon, statusText, desc) = when {
        status == OrderStatus.PENDING -> Quad(
            BrandOrange, Icons.Default.Payment, "Menunggu Pembayaran",
            "Selesaikan pembayaran untuk ${status.name} toko sekaligus."
        )
        status == OrderStatus.PAID || status == OrderStatus.PROCESSED -> {
            when (pickupStatus) {
                "READY" -> Quad(
                    BrandYellow,
                    Icons.Default.Store,
                    "Siap Diambil",
                    "Satu atau lebih toko siap untuk diambil."
                )
                "PICKED_UP" -> Quad(
                    BrandGreen,
                    Icons.Default.Check,
                    "Selesai Diambil",
                    "Semua barang telah berhasil diambil."
                )
                else -> Quad(
                    BrandBlue,
                    Icons.Default.Inventory2,
                    "Diproses",
                    "Penjual sedang menyiapkan barang pesananmu."
                )
            }
        }
        status == OrderStatus.COMPLETED -> Quad(
            BrandGreen,
            Icons.Default.Check,
            "Transaksi Selesai",
            "Terima kasih telah berbelanja!"
        )
        else -> Quad(
            BrandRed,
            Icons.Default.Inventory2,
            "Dibatalkan",
            "Transaksi ini telah dibatalkan."
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyEffect(isDark, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        // [PERBAIKAN] Tambahkan fillMaxWidth() agar Column memenuhi Box dan Align Center bekerja
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = statusColor.copy(alpha = 0.1f),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(statusIcon, null, modifier = Modifier.size(32.dp), tint = statusColor)
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
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun GroupInfoSection(
    context: Context,
    groupId: String?,
    date: String,
    method: String,
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
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Header Row (ID)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Group ID",
                        style = MaterialTheme.typography.labelMedium,
                        color = subTextColor
                    )
                    // [SAFE CALL] Pastikan tidak error
                    val displayId = groupId?.takeLast(6)?.uppercase() ?: "-"
                    Text(
                        "#$displayId",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = textColor
                    )
                }
                if (groupId != null) {
                    IconButton(
                        onClick = {
                            val clipboard =
                                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Group ID", groupId)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Group ID Disalin", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            null,
                            tint = BrandGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            HorizontalDivider(
                color = if (isDark) Color.White.copy(0.1f) else Color.Black.copy(0.05f)
            )

            // Info Details
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Tanggal",
                        style = MaterialTheme.typography.labelMedium,
                        color = subTextColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        date,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = textColor
                    )
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(
                        "Metode",
                        style = MaterialTheme.typography.labelMedium,
                        color = subTextColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        method,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun StoreOrderCardGlossy(
    order: Order,
    isDark: Boolean,
    textColor: Color,
    subTextColor: Color,
    brandGreen: Color,
    onDetailClick: () -> Unit,
    onMapsClick: () -> Unit
) {
    val isReadyToPickup =
        (order.status == OrderStatus.PAID || order.status == OrderStatus.PROCESSED) && order.pickupStatus != "PICKED_UP"
    val (statusColor, statusIcon, statusText) = when (order.pickupStatus) {
        "READY" -> Triple(BrandYellow, Icons.Default.Store, "Siap Diambil")
        "PICKED_UP" -> Triple(brandGreen, Icons.Default.CheckCircle, "Selesai")
        else -> Triple(BrandBlue, Icons.Default.Inventory2, "Diproses")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glossyEffect(isDark, RoundedCornerShape(20.dp))
            .clickable { onDetailClick() }
            .padding(16.dp)
    ) {
        Column {
            // Header Toko
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = brandGreen.copy(alpha = 0.1f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Filled.Store,
                                null,
                                tint = brandGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        order.outletName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = textColor
                    )
                }

                // Badge Status
                Surface(
                    shape = RoundedCornerShape(50),
                    color = statusColor.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            statusIcon,
                            null,
                            tint = statusColor,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            statusText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = if (isDark) Color.White.copy(0.1f) else Color.Black.copy(0.05f)
            )

            // Daftar Item
            order.items.take(2).forEachIndexed { index, item ->
                GroupProductItemGlossy(item, isDark, textColor, subTextColor)
                if (index < order.items.size - 1 && index < 1) Spacer(modifier = Modifier.height(12.dp))
            }

            if (order.items.size > 2) {
                Text(
                    text = "+ ${order.items.size - 2} produk lainnya...",
                    fontSize = 11.sp,
                    color = subTextColor,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Tombol Aksi per Toko
            if (isReadyToPickup) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onMapsClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (order.pickupStatus == "READY") BrandYellow else BrandBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) {
                    Icon(
                        Icons.Default.Map,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (order.pickupStatus == "READY") "Ambil Barang" else "Lacak Toko Ini",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subtotal", fontSize = 12.sp, color = subTextColor)
                    Text(
                        text = String.format(Locale("id", "ID"), "Rp %,d", order.totalAmount),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun GroupPaymentDetailSection(
    totalAmount: Long,
    itemCount: Int,
    paymentMethod: String,
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
                "Rincian Pembayaran",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Total Item ($itemCount)", color = subTextColor, fontSize = 14.sp)
                Text(
                    String.format(Locale("id", "ID"), "Rp %,d", totalAmount),
                    color = textColor,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Biaya Layanan", color = subTextColor, fontSize = 14.sp)
                Text("Rp 0", color = BrandGreen, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = if (isDark) Color.White.copy(0.2f) else Color.Black.copy(0.1f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Total Belanja",
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 16.sp
                )
                Text(
                    text = String.format(Locale("id", "ID"), "Rp %,d", totalAmount),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = BrandGreen
                )
            }
        }
    }
}

@Composable
fun GroupPaymentBottomBar(
    totalAmount: Long,
    buttonText: String,
    btnColor: Color,
    isDark: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Surface(
        color = if (isDark) Color.Black.copy(0.8f) else Color.White.copy(0.9f),
        shadowElevation = 24.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            // Total Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total Tagihan",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isDark) Color.LightGray else Color.Gray
                )
                Text(
                    String.format(Locale("id", "ID"), "Rp %,d", totalAmount),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = BrandOrange
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Action Button
            Button(
                onClick = onClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = btnColor,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                if (icon != null) {
                    Icon(icon, null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(buttonText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

// Data Class Helper
data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun GroupProductItemGlossy(
    item: OrderItem,
    isDark: Boolean,
    textColor: Color,
    subTextColor: Color
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(64.dp),
            color = if (isDark) Color.White.copy(0.05f) else Color.Black.copy(0.05f)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(item.image).crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.productName,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${item.quantity} x ${String.format(Locale("id", "ID"), "Rp %,d", item.price)}",
                color = subTextColor,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun StatusBadgeGroup(
    status: OrderStatus, // [FIX] Ubah parameter dari String ke OrderStatus
    modifier: Modifier = Modifier
) {
    // Tentukan warna dan teks berdasarkan Enum OrderStatus
    val (backgroundColor, contentColor, text) = when (status) {
        OrderStatus.PAID -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "PAID")       // Hijau
        OrderStatus.PENDING -> Triple(Color(0xFFFFF3E0), Color(0xFFEF6C00), "UNPAID")  // Orange
        OrderStatus.CANCELLED -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "CANCELED") // Merah
        OrderStatus.COMPLETED -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "COMPLETE") // Hijau
        else -> Triple(Color.LightGray.copy(alpha = 0.3f), Color.DarkGray, status.name)
    }

    Box(
        modifier = modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        )
    }
}