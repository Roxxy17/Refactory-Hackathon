package com.example.kalanacommerce.presentation.screen.dashboard.detail.success

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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
        // Load konfigurasi OSM
        Configuration.getInstance().load(context, context.getSharedPreferences("osm_pref", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName

        // Load Data Lokasi
        viewModel.loadData(orderId, paymentGroupId)
    }

    Scaffold(
        bottomBar = {
            // Tombol Selesai Floating
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = BrandGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Pembayaran Berhasil!", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Text("Pesananmu akan segera diproses.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                    ) {
                        Text("Lihat Riwayat Pesanan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandGreen)
                }
            } else {
                // TAMPILAN PETA MULTI-POINT
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)

                            val userLoc = uiState.userLocation ?: GeoPoint(-7.77, 110.37) // Default
                            controller.setZoom(13.0)
                            controller.setCenter(userLoc)

                            // 1. Marker User
                            val userMarker = Marker(this).apply {
                                position = userLoc
                                title = "Lokasi Kamu"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            }
                            overlays.add(userMarker)

                            // 2. Markers Toko
                            uiState.storeLocations.forEachIndexed { index, storeLoc ->
                                val storeMarker = Marker(this).apply {
                                    position = storeLoc
                                    title = "Toko ${index + 1}"
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    // icon = ContextCompat.getDrawable(ctx, R.drawable.ic_store_pin) // Bisa custom icon
                                }
                                overlays.add(storeMarker)
                            }

                            // 3. Draw Route (User -> Toko 1 -> Toko 2)
                            // Note: Urutan logis biasanya Toko -> User, tapi requestmu User -> Toko
                            scope.launch(Dispatchers.IO) {
                                try {
                                    val roadManager = OSRMRoadManager(ctx, "KalanaAgent")
                                    val waypoints = ArrayList<GeoPoint>()

                                    // Tambahkan urutan titik
                                    waypoints.add(userLoc) // Titik Awal (User)
                                    waypoints.addAll(uiState.storeLocations) // Titik Berikutnya (Toko-toko)

                                    if (waypoints.size >= 2) {
                                        val road = roadManager.getRoad(waypoints)
                                        if (road.mStatus == org.osmdroid.bonuspack.routing.Road.STATUS_OK) {
                                            val roadOverlay = RoadManager.buildRoadOverlay(road)
                                            roadOverlay.outlinePaint.color = android.graphics.Color.BLUE
                                            roadOverlay.outlinePaint.strokeWidth = 10f

                                            withContext(Dispatchers.Main) {
                                                overlays.add(roadOverlay)
                                                invalidate()
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}