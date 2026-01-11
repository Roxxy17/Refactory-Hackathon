package com.example.kalanacommerce.presentation.screen.dashboard.detail.checkout

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import com.example.kalanacommerce.domain.model.CartItem
import com.example.kalanacommerce.presentation.screen.dashboard.cart.BrandGreen
import com.example.kalanacommerce.presentation.screen.dashboard.cart.BrandOrange
import com.example.kalanacommerce.presentation.screen.dashboard.cart.formatRupiah
import org.koin.androidx.compose.koinViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    itemIdsString: String,
    viewModel: CheckoutViewModel = koinViewModel(),
    themeSetting: ThemeSetting,
    onBackClick: () -> Unit,
    onNavigateToPayment: (String, String) -> Unit,
    onNavigateToAddress: () -> Unit
) {
    LaunchedEffect(itemIdsString) {
        if (itemIdsString.isNotEmpty()) {
            viewModel.loadCheckoutItems(itemIdsString)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadAddresses()
    }

    val uiState by viewModel.uiState.collectAsState()
    val timelineState by viewModel.timelineState.collectAsState()
    val storeLocationState by viewModel.storeLocationState.collectAsState() // [BARU] Collect Lokasi Toko

    var showAddressSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val systemInDark = isSystemInDarkTheme()
    val isDarkActive = remember(themeSetting, systemInDark) {
        when (themeSetting) {
            ThemeSetting.LIGHT -> false
            ThemeSetting.DARK -> true
            ThemeSetting.SYSTEM -> systemInDark
        }
    }

    val backgroundColor = if (isDarkActive) MaterialTheme.colorScheme.background else Color(0xFFF5F6F8)
    val contentColor = if (isDarkActive) Color.White else Color.Black
    val cardColor = if (isDarkActive) MaterialTheme.colorScheme.surface else Color.White

    LaunchedEffect(uiState.checkoutResult) {
        uiState.checkoutResult?.let { result ->
            val paymentUrl = result.snapRedirectUrl
            val orderId = result.id

            if (paymentUrl.isNotEmpty() && orderId.isNotEmpty()) {
                // 1. Lakukan navigasi
                onNavigateToPayment(paymentUrl, orderId)

                // 2. Reset state agar tidak trigger ulang atau nyangkut
                viewModel.onPaymentNavigationHandled()
            }
        }
    }

    val totalSaved = remember(uiState.checkoutItems) {
        uiState.checkoutItems.sumOf { item ->
            if (item.originalPrice > item.price) {
                (item.originalPrice - item.price) * item.quantity
            } else {
                0L
            }
        }
    }

    LaunchedEffect(uiState.checkoutResult) {
        uiState.checkoutResult?.let { result ->
            // Pastikan model CheckoutResult kamu punya field 'snapRedirectUrl' dan 'id'
            // Jika error 'snapRedirectUrl' tidak ada, ganti dengan 'snapToken' (tapi PaymentScreen butuh URL)

            val paymentUrl = result.snapRedirectUrl // Gunakan URL redirect dari Midtrans
            val orderId = result.id // Gunakan ID order

            if (paymentUrl.isNotEmpty() && orderId.isNotEmpty()) {
                onNavigateToPayment(paymentUrl, orderId)
            }
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Konfirmasi Pesanan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = cardColor,
                    titleContentColor = contentColor,
                    navigationIconContentColor = contentColor
                )
            )
        },
        bottomBar = {
            if (!uiState.isLoading && uiState.checkoutItems.isNotEmpty()) {
                CheckoutBottomBar(
                    total = uiState.totalPayment,
                    saved = totalSaved,
                    itemCount = uiState.checkoutItems.sumOf { it.quantity },
                    isLoading = uiState.isLoading,
                    onPayClick = { viewModel.placeOrder() }
                )
            }
        }
    ) { paddingValues ->

        if (uiState.isLoading && uiState.checkoutItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. ALAMAT
                item {
                    SectionHeader("Lokasi Kamu")
                    AddressSection(
                        address = uiState.selectedAddress,
                        cardColor = cardColor,
                        textColor = contentColor,
                        onClick = { showAddressSheet = true }
                    )
                }

                // 2. TIMELINE (Estimasi Waktu)
                item {
                    SectionHeader("Estimasi Proses (Ambil Sendiri)")
                    PickupTimelineCard(
                        state = timelineState,
                        cardColor = cardColor,
                        textColor = contentColor
                    )
                }

                // [BARU] 3. LOKASI TOKO (Di Bawah Timeline)
                item {
                    SectionHeader("Titik Pengambilan")
                    StoreLocationCard(
                        state = storeLocationState,
                        cardColor = cardColor,
                        textColor = contentColor
                    )
                }

                // 4. ITEMS
                val groupedItems = uiState.checkoutItems.groupBy { it.outletName }
                groupedItems.forEach { (outletName, items) ->
                    item {
                        CheckoutStoreCard(
                            outletName = outletName,
                            items = items,
                            cardColor = cardColor,
                            textColor = contentColor,
                            onUpdateQty = { id, qty -> viewModel.updateQuantity(id, qty) }
                        )
                    }
                }

                // 5. PAYMENT & PROMO & SUMMARY
                item {
                    SectionHeader("Metode Pembayaran")
                    PaymentMethodInfoCard(cardColor, contentColor)
                }

                item {
                    GeneralOptionCard(
                        icon = Icons.Default.ConfirmationNumber,
                        title = "Pakai Voucher Hemat",
                        subtitle = "Lihat promo tersedia",
                        cardColor = cardColor,
                        iconColor = BrandOrange,
                        onClick = { /* TODO */ }
                    )
                }

                item {
                    SectionHeader("Rincian Biaya")
                    CostSummarySection(uiState, cardColor, contentColor)
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

    // --- BOTTOM SHEET ---
    if (showAddressSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddressSheet = false },
            sheetState = sheetState,
            containerColor = if (isDarkActive) MaterialTheme.colorScheme.surface else Color.White
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 20.dp)) {
                Text("Pilih Alamat Pengambilan", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = contentColor, modifier = Modifier.padding(bottom = 16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f, fill = false)) {
                    items(uiState.availableAddresses) { address ->
                        AddressSelectionItem(
                            address = address,
                            isSelected = address.id == uiState.selectedAddress?.id,
                            textColor = contentColor,
                            onClick = {
                                viewModel.selectAddress(address) // Trigger ganti alamat & refresh mock data
                                showAddressSheet = false
                            }
                        )
                    }
                    item {
                        OutlinedButton(
                            onClick = { showAddressSheet = false; onNavigateToAddress() },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            border = BorderStroke(1.dp, BrandGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, null, tint = BrandGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tambah / Kelola Alamat", color = BrandGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// --- NEW COMPONENT: STORE LOCATION CARD ---
@Composable
fun StoreLocationCard(
    state: StoreLocationModel,
    cardColor: Color,
    textColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Map
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(BrandGreen.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Map, null, tint = BrandGreen, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Detail Toko
            Column(modifier = Modifier.weight(1f)) {
                Text(state.name, fontWeight = FontWeight.Bold, color = textColor)
                Spacer(modifier = Modifier.height(2.dp))
                Text(state.address, style = MaterialTheme.typography.bodySmall, color = textColor.copy(alpha = 0.7f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DirectionsWalk, null, tint = BrandOrange, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${state.distance} dari lokasimu", style = MaterialTheme.typography.labelSmall, color = BrandOrange)
                }
            }
        }
    }
}

// --- COMPONENTS ---

@Composable
fun AddressSelectionItem(
    address: AddressUiModel,
    isSelected: Boolean,
    textColor: Color,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) BrandOrange else Color.LightGray.copy(0.5f)
    val bgColor = if (isSelected) BrandOrange.copy(0.05f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isSelected) BrandOrange else Color.Gray,
            modifier = Modifier.size(20.dp).padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = address.label ?: "Alamat",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )
                if (address.isMain) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(color = BrandGreen.copy(0.1f), shape = RoundedCornerShape(4.dp)) {
                        Text("Utama", modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), color = BrandGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "${address.name} • ${address.phone}",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor.copy(0.8f)
            )
            Text(
                address.address,
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(0.6f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PickupTimelineCard(
    state: PickupTimelineState,
    cardColor: Color,
    textColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, null, tint = BrandGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        "Total Estimasi: ±${state.totalTime} Menit",
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    // Tampilkan info jika lebih dari 1 toko
                    if (state.storeCount > 1) {
                        Text(
                            "Mengunjungi ${state.storeCount} Toko",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandOrange
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            TimelineItem("Pesanan Disiapkan", "${state.prepTime} Menit", Icons.Default.Inventory2, false, textColor)
            TimelineItem("Perjalanan Rute", "${state.travelTime} Menit", Icons.Default.DirectionsWalk, false, textColor)
            TimelineItem("Proses Ambil", "${state.pickupTime} Menit", Icons.Default.Storefront, true, textColor)
        }
    }
}

@Composable
fun TimelineItem(title: String, time: String, icon: ImageVector, isLast: Boolean, textColor: Color) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(32.dp)) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(BrandGreen.copy(0.1f), CircleShape)
                    .border(1.dp, BrandGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = BrandGreen, modifier = Modifier.size(12.dp))
            }
            if (!isLast) {
                Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(BrandGreen.copy(0.3f)).padding(vertical = 4.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 24.dp)) {
            Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = textColor)
            Text(time, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
fun PaymentMethodInfoCard(cardColor: Color, textColor: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Payments, null, tint = BrandOrange, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Metode Pembayaran Tersedia", fontWeight = FontWeight.Bold, color = textColor)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Mendukung QRIS, GoPay, ShopeePay, dan Transfer Bank.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("QRIS", "BCA", "BRI", "Mandiri").forEach {
                    Surface(color = Color(0xFFF5F6F8), shape = RoundedCornerShape(4.dp), border = BorderStroke(1.dp, Color.LightGray)) {
                        Text(it, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
        modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
    )
}

@Composable
fun AddressSection(address: AddressUiModel?, cardColor: Color, textColor: Color, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(BrandOrange.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.LocationOn, null, tint = BrandOrange, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (address != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(address.label ?: "Alamat Utama", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = BrandOrange)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${address.name} • ${address.phone}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = textColor)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(address.address, style = MaterialTheme.typography.bodySmall, color = textColor.copy(0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                } else {
                    Text("Pilih Lokasi Kamu", fontWeight = FontWeight.Bold, color = textColor)
                    Text("Agar kami bisa hitung jarak ke toko", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.Gray)
        }
    }
}

@Composable
fun CheckoutStoreCard(outletName: String, items: List<CartItem>, cardColor: Color, textColor: Color, onUpdateQty: (String, Int) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Store, null, tint = BrandGreen, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(outletName, fontWeight = FontWeight.Bold, color = textColor)
            }
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(0.3f))
            items.forEachIndexed { index, item ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Card(shape = RoundedCornerShape(8.dp), modifier = Modifier.size(60.dp)) {
                        AsyncImage(model = item.productImage, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.productName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), maxLines = 1, overflow = TextOverflow.Ellipsis, color = textColor)
                        Text("${item.quantity} x ${formatRupiah(item.price)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Text(formatRupiah(item.price * item.quantity), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = textColor)
                }
                if (index < items.size - 1) Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun GeneralOptionCard(icon: ImageVector, title: String, subtitle: String, cardColor: Color, iconColor: Color, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = iconColor)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.Gray)
        }
    }
}

@Composable
fun CostSummarySection(uiState: CheckoutUiState, cardColor: Color, textColor: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Subtotal Produk", style = MaterialTheme.typography.bodyMedium, color = textColor.copy(0.7f))
                Text(formatRupiah(uiState.subtotal), style = MaterialTheme.typography.bodyMedium, color = textColor)
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Biaya Layanan", style = MaterialTheme.typography.bodyMedium, color = textColor.copy(0.7f))
                Text(formatRupiah(1000), style = MaterialTheme.typography.bodyMedium, color = textColor)
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(0.3f))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Total Pembayaran", fontWeight = FontWeight.Bold, color = textColor)
                Text(formatRupiah(uiState.totalPayment + 1000), fontWeight = FontWeight.Bold, color = BrandOrange, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun CheckoutBottomBar(total: Long, saved: Long, itemCount: Int, isLoading: Boolean, onPayClick: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 16.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Total Tagihan", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(formatRupiah(total + 1000), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = BrandOrange)
                if (saved > 0) {
                    Text("Hemat ${formatRupiah(saved)}", style = MaterialTheme.typography.labelSmall, color = BrandGreen)
                }
            }
            Button(onClick = onPayClick, enabled = !isLoading, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = BrandOrange), modifier = Modifier.height(48.dp)) {
                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp)) else {
                    Text("Bayar", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ConfirmationNumber, null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}