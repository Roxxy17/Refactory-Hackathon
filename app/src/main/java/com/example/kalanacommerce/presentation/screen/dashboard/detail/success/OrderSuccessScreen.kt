// File: presentation/screen/dashboard/detail/success/OrderSuccessScreen.kt
package com.example.kalanacommerce.presentation.screen.dashboard.detail.success

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.kalanacommerce.domain.model.Order
import com.example.kalanacommerce.presentation.screen.dashboard.detail.payment.BrandGreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

val WarningOrange = Color(0xFFFFA000)
val NeutralGray = Color(0xFFF0F0F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSuccessScreen(
    orderId: String?,
    paymentGroupId: String?,
    viewModel: OrderSuccessViewModel = koinViewModel(),
    onNavigateToHistory: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    // State Bottom Sheet
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded
        )
    )

    // Inisialisasi MapView di luar AndroidView agar bisa direferensikan ulang
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
        }
    }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osm_pref", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
        viewModel.loadData(orderId, paymentGroupId)
    }

    // [PENTING] Efek untuk menggambar ulang Peta saat data lokasi berubah
    LaunchedEffect(uiState.storeLocations, uiState.userLocation) {
        if (uiState.userLocation != null && uiState.storeLocations.isNotEmpty()) {
            mapView.overlays.clear() // Hapus marker lama

            val userLoc = uiState.userLocation!!
            mapView.controller.setCenter(userLoc)

            // 1. Gambar Marker User
            val userMarker = Marker(mapView).apply {
                position = userLoc
                title = "Lokasi Kamu"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            mapView.overlays.add(userMarker)

            // 2. Gambar Marker Toko (Looping)
            uiState.storeLocations.forEachIndexed { index, storeLoc ->
                val storeMarker = Marker(mapView).apply {
                    position = storeLoc
                    title = "Toko ${index + 1}"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = context.resources.getDrawable(org.osmdroid.library.R.drawable.marker_default, null) // Pastikan icon ada
                }
                mapView.overlays.add(storeMarker)
            }

            // 3. Gambar Rute (Async)
            scope.launch(Dispatchers.IO) {
                try {
                    val roadManager = OSRMRoadManager(context, "KalanaAgent")
                    val waypoints = ArrayList<GeoPoint>()
                    waypoints.add(userLoc)
                    waypoints.addAll(uiState.storeLocations)

                    if (waypoints.size >= 2) {
                        val road = roadManager.getRoad(waypoints)
                        if (road.mStatus == org.osmdroid.bonuspack.routing.Road.STATUS_OK) {
                            val roadOverlay = RoadManager.buildRoadOverlay(road)
                            roadOverlay.outlinePaint.color = android.graphics.Color.BLUE
                            roadOverlay.outlinePaint.strokeWidth = 12f

                            withContext(Dispatchers.Main) {
                                mapView.overlays.add(roadOverlay)
                                mapView.invalidate() // Refresh peta
                            }
                        }
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
            mapView.invalidate()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 140.dp,
        sheetContainerColor = Color.White,
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
        sheetShadowElevation = 16.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetDragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.6f))
                )
            }
        },
        sheetContent = {
            // Konten Bottom Sheet (List Kartu)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                    // HEADER (Klik untuk Expand/Collapse)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                scope.launch {
                                    if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                        scaffoldState.bottomSheetState.partialExpand()
                                    } else {
                                        scaffoldState.bottomSheetState.expand()
                                    }
                                }
                            }
                            .padding(bottom = 16.dp, top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Status Pesanan",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${uiState.orders.size} Toko • Estimasi Siap 15 Menit",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        val rotationState by animateFloatAsState(
                            targetValue = if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) 180f else 0f,
                            label = "ArrowRotation"
                        )
                        Icon(
                            imageVector = Icons.Default.ExpandLess,
                            contentDescription = "Expand",
                            modifier = Modifier.size(28.dp).rotate(rotationState),
                            tint = Color.Gray
                        )
                    }

                    Divider(color = NeutralGray)
                    Spacer(modifier = Modifier.height(16.dp))

                    // CONTENT LIST
                    if (uiState.isLoading) {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = BrandGreen)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 24.dp)
                        ) {
                            items(uiState.orders) { order ->
                                OrderSimulationCard(
                                    order = order,
                                    onUpdateStatus = { newStatus ->
                                        viewModel.updateOrderPickupStatus(order.id, newStatus)
                                    }
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                if (uiState.isAllOrdersCompleted) {
                                    Button(
                                        onClick = onNavigateToHistory,
                                        modifier = Modifier.fillMaxWidth().height(56.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                                        elevation = ButtonDefaults.buttonElevation(8.dp)
                                    ) {
                                        Text("Selesai & Lihat Riwayat", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                } else {
                                    Surface(
                                        color = NeutralGray,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Selesaikan semua status di atas untuk melanjutkan.",
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(12.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        // MAP Container
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { mapView } // Gunakan instance mapView yang sudah di-remember
            )
        }
    }
}

// [MODERN CARD DESIGN - TETAP SAMA]
@Composable
fun OrderSimulationCard(
    order: Order,
    onUpdateStatus: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Toko
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = BrandGreen.copy(alpha = 0.1f),
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = order.outletName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Kode: ${order.orderCode.takeLast(8)}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = NeutralGray)

            // Status & Actions
            when (order.pickupStatus) {
                "PROCESS" -> {
                    StatusStep(
                        icon = Icons.Default.Inventory2,
                        color = Color.Blue,
                        title = "Pesanan Diproses",
                        desc = "Penjual sedang menyiapkan pesanan."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onUpdateStatus("READY") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NeutralGray, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Simulasi: Set Barang Siap")
                    }
                }
                "READY" -> {
                    StatusStep(
                        icon = Icons.Default.Store,
                        color = WarningOrange,
                        title = "Siap Diambil",
                        desc = "Silakan datang ke toko & tunjukkan kode pesanan."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onUpdateStatus("PICKED_UP") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Konfirmasi Ambil Barang")
                    }
                }
                "PICKED_UP" -> {
                    StatusStep(
                        icon = Icons.Default.CheckCircle,
                        color = BrandGreen,
                        title = "Selesai",
                        desc = "Barang berhasil diambil."
                    )
                }
                else -> {
                    StatusStep(
                        icon = Icons.Default.Info,
                        color = Color.Red,
                        title = "Dibatalkan",
                        desc = "Pesanan ini telah dibatalkan."
                    )
                }
            }
        }
    }
}

@Composable
fun StatusStep(icon: ImageVector, color: Color, title: String, desc: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
            Text(desc, fontSize = 12.sp, color = Color.Gray)
        }
    }
}