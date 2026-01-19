package com.example.kalanacommerce.presentation.screen.dashboard.map

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapRouteScreen(
    userLat: Double,
    userLong: Double,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 1. Setup Konfigurasi OSM (Wajib agar peta loading)
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osm_pref", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
    }

    // --- DATA DUMMY LOKASI ---
    // Ganti ini dengan data real dari API nanti
    val finalUserLat = if (userLat != 0.0) userLat else -7.7717
    val finalUserLong = if (userLong != 0.0) userLong else 110.377

    val userLocation = GeoPoint(finalUserLat, finalUserLong)

    // Lokasi Toko (Hardcode atau ambil dari API Toko jika ada)
    val storeLocation = GeoPoint(-7.7956, 110.365)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lokasi Toko") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            // 2. TAMPILKAN PETA (AndroidView)
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)

                        // Set Zoom Awal ke User
                        controller.setZoom(10.0)
                        controller.setCenter(userLocation)

                        // A. Buat Marker User
                        val startMarker = Marker(this).apply {
                            position = userLocation
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = "Posisi Kamu"
                            // icon = ctx.getDrawable(R.drawable.ic_user_pin) // Bisa ganti icon custom
                        }
                        overlays.add(startMarker)

                        // B. Buat Marker Toko
                        val endMarker = Marker(this).apply {
                            position = storeLocation
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = "Toko Kalana"
                        }
                        overlays.add(endMarker)

                        // C. HITUNG RUTE (Di Background Thread)
                        scope.launch(Dispatchers.IO) {
                            try {
                                // Gunakan OSRM (Gratis)
                                val roadManager = OSRMRoadManager(ctx, "KalanaCommerce-Agent")
                                val waypoints = arrayListOf(userLocation, storeLocation)

                                // Ambil data jalan
                                val road = roadManager.getRoad(waypoints)

                                if (road.mStatus == org.osmdroid.bonuspack.routing.Road.STATUS_OK) {
                                    // Buat Garis (Polyline)
                                    val roadOverlay = RoadManager.buildRoadOverlay(road)
                                    roadOverlay.outlinePaint.color = Color.BLUE // Warna Garis
                                    roadOverlay.outlinePaint.strokeWidth = 10f // Ketebalan

                                    // Update UI di Main Thread
                                    withContext(Dispatchers.Main) {
                                        overlays.add(roadOverlay)
                                        invalidate() // Refresh peta
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            )

            // Info Tambahan (Floating Card)
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    text = "Estimasi jarak garis lurus: Sedang memuat rute...",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}