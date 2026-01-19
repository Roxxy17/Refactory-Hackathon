package com.example.kalanacommerce.presentation.screen.dashboard.map

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerScreen(
    initialLat: Double = 0.0,
    initialLong: Double = 0.0,
    onConfirmLocation: (Double, Double) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    // State untuk menyimpan koordinat tengah peta saat ini
    var currentCenter by remember {
        mutableStateOf(
            if (initialLat != 0.0) GeoPoint(initialLat, initialLong)
            else GeoPoint(-6.200000, 106.816666) // Default Jakarta/Indonesia
        )
    }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osm_pref", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Titik Lokasi") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            // 1. PETA OSM
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(18.0)
                        controller.setCenter(currentCenter)

                        // Listener saat peta digeser
                        addMapListener(object : MapListener {
                            override fun onScroll(event: ScrollEvent?): Boolean {
                                val center = mapCenter as GeoPoint
                                currentCenter = center
                                return true
                            }
                            override fun onZoom(event: ZoomEvent?): Boolean = true
                        })
                    }
                }
            )

            // 2. PIN STATIS DI TENGAH (Marker)
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Pin Lokasi",
                tint = Color.Red,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center) // Selalu di tengah layar
                    .offset(y = (-24).dp) // Offset ke atas sedikit agar ujung pin pas di tengah
            )

            // 3. TOMBOL KONFIRMASI (Floating di Bawah)
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Koordinat Terpilih:",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "${currentCenter.latitude}, ${currentCenter.longitude}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // Kirim lat/long balik ke screen sebelumnya
                            onConfirmLocation(currentCenter.latitude, currentCenter.longitude)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pilih Lokasi Ini")
                    }
                }
            }
        }
    }
}