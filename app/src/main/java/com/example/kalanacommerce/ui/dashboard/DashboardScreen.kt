package com.example.kalanacommerce.ui.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kalanacommerce.navigation.BottomBarScreen
import com.example.kalanacommerce.ui.components.AppBottomNavigationBar

/**
 * Layar utama setelah login, yang menampung bottom navigation dan konten halaman.
 */
@Composable
fun DashboardScreen(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    // HANYA ADA SATU SCAFFOLD DI SINI
    Scaffold(
        // Semua logika Bottom Bar dan FAB sekarang ada di dalam komponen ini
        bottomBar = {
            AppBottomNavigationBar(navController = navController) { screen ->
                // Kode BARU
                navController.navigate(screen.route) {
                    // Properti ini adalah bagian dari NavOptionsBuilder (blok navigate), bukan popUpTo
                    launchSingleTop = true
                    restoreState = true

                    popUpTo(navController.graph.startDestinationId) {
                        // Opsi 'saveState' ada di dalam blok popUpTo
                        saveState = true
                    }
                }

            }
        },
        // FloatingActionButton dan posisinya sudah tidak didefinisikan di sini lagi
        content = { innerPadding ->
            // Navigasi untuk konten-konten di dalam Dashboard (Eksplor, Profil, dll.)
            DashboardNavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                onLogout = onLogout // Teruskan fungsi onLogout ke NavGraph
            )
        }
    )
}

/**
 * NavHost untuk halaman-halaman yang diakses dari Bottom Navigation Bar.
 */
@Composable
fun DashboardNavGraph(
    navController: NavHostController,
    modifier: Modifier,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Eksplor.route,
        modifier = modifier
    ) {
        // --- Kode yang Diperbaiki ---
        composable(BottomBarScreen.Eksplor.route) {
            // TIDAK PERLU meneruskan containerColor.
            // ExploreScreen sudah menggunakan Scaffold di dalamnya untuk mengatur warna latar belakang.
            ExploreScreen(modifier = Modifier.fillMaxSize())
        }

        composable(BottomBarScreen.Pencarian.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Halaman Pencarian")
            }
        }
        composable(BottomBarScreen.Riwayat.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Halaman Riwayat")
            }
        }
        composable(BottomBarScreen.Profile.route) {
            ProfileContent(onLogout = onLogout)
        }
    }
}

/**
 * Composable untuk isi dari Halaman Profile, termasuk tombol Logout.
 */
@Composable
fun ProfileContent(onLogout: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Halaman Profile")
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onLogout, // Panggil fungsi logout saat tombol diklik
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)) // Warna merah
            ) {
                Text("Logout")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen(onLogout = {})
}
