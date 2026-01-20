// File: presentation/screen/dashboard/detail/success/OrderSuccessScreen.kt
package com.example.kalanacommerce.presentation.screen.dashboard.detail.success

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.kalanacommerce.domain.model.Order
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
import com.example.kalanacommerce.presentation.screen.dashboard.detail.payment.BrandGreen

val WarningOrange = Color(0xFFFFA000)

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

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osm_pref", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
        viewModel.loadData(orderId, paymentGroupId)
    }

    Scaffold(
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                // Panel setinggi 50% layar agar muat list
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f)
            ) {
                Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                    // Handle Panel (Garis Kecil)
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.width(40.dp).height(4.dp).background(Color.LightGray, CircleShape))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Status Pesanan (${uiState.orders.size} Toko)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // [LIST KARTU PER TOKO]
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.orders) { order ->
                            OrderSimulationCard(
                                order = order,
                                onUpdateStatus = { newStatus ->
                                    viewModel.updateOrderPickupStatus(order.id, newStatus)
                                }
                            )
                        }
                    }

                    // [TOMBOL FINAL] Hanya muncul jika SEMUA toko sudah selesai
                    if (uiState.isAllOrdersCompleted) {
                        Button(
                            onClick = onNavigateToHistory,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                        ) {
                            Text("Selesai - Lihat Riwayat", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Info Text jika belum selesai semua
                        Text(
                            "Selesaikan status semua toko untuk melihat riwayat.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 16.dp).align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        // ... (Bagian MAPS Tetap Sama / Copy Paste yang lama) ...
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandGreen)
                }
            } else {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            val userLoc = uiState.userLocation ?: GeoPoint(-7.77, 110.37)
                            controller.setZoom(14.0); controller.setCenter(userLoc)

                            // Marker User
                            overlays.add(Marker(this).apply {
                                position = userLoc; title = "Lokasi Kamu"; setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            })

                            // Marker Toko Loop
                            uiState.storeLocations.forEachIndexed { index, storeLoc ->
                                overlays.add(Marker(this).apply {
                                    position = storeLoc; title = "Toko ${index + 1}"; setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                })
                            }

                            // Route
                            scope.launch(Dispatchers.IO) {
                                try {
                                    val roadManager = OSRMRoadManager(ctx, "KalanaAgent")
                                    val waypoints = ArrayList<GeoPoint>()
                                    waypoints.add(userLoc)
                                    waypoints.addAll(uiState.storeLocations)
                                    if (waypoints.size >= 2) {
                                        val road = roadManager.getRoad(waypoints)
                                        if (road.mStatus == org.osmdroid.bonuspack.routing.Road.STATUS_OK) {
                                            val roadOverlay = RoadManager.buildRoadOverlay(road)
                                            roadOverlay.outlinePaint.color = android.graphics.Color.BLUE
                                            roadOverlay.outlinePaint.strokeWidth = 10f
                                            withContext(Dispatchers.Main) { overlays.add(roadOverlay); invalidate() }
                                        }
                                    }
                                } catch (e: Exception) { e.printStackTrace() }
                            }
                        }
                    }
                )
            }
        }
    }
}

// [KOMPONEN KARTU SIMULASI PER ITEM]
@Composable
fun OrderSimulationCard(
    order: Order,
    onUpdateStatus: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Toko
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Store, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(order.outletName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.weight(1f))
                Text("#${order.orderCode.takeLast(5)}", fontSize = 10.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Logic Tampilan Tombol Berdasarkan Status Per Item
            when(order.pickupStatus) {
                "PROCESS" -> {
                    StatusRow(Icons.Default.Inventory2, Color.Blue, "Diproses", "Penjual menyiapkan barang")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onUpdateStatus("READY") },
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("[Simulasi] Set Barang Siap", fontSize = 12.sp)
                    }
                }
                "READY" -> {
                    StatusRow(Icons.Default.Store, WarningOrange, "Siap Diambil", "Silakan ambil di toko")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onUpdateStatus("PICKED_UP") },
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Ambil Barang (Konfirmasi)", fontSize = 12.sp)
                    }
                }
                "PICKED_UP" -> {
                    StatusRow(Icons.Default.CheckCircle, BrandGreen, "Selesai", "Barang sudah diambil")
                }
                else -> { // Cancelled
                    StatusRow(Icons.Default.Info, Color.Red, "Dibatalkan", "Pesanan batal")
                }
            }
        }
    }
}

@Composable
fun StatusRow(icon: ImageVector, color: Color, title: String, desc: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
            Text(desc, fontSize = 12.sp, color = Color.Gray)
        }
    }
}