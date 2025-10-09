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
// Impor ini sekarang akan merujuk ke fungsi yang benar dari file lain,
// karena kita akan menghapus duplikatnya dari file ini.
// Pastikan path package-nya benar jika Anda meletakkannya di tempat lain.
import com.example.kalanacommerce.ui.dashboard.ExploreScreen

/**
 * Layar utama setelah login, yang menampung bottom navigation dan konten halaman.
 */
@Composable
fun DashboardScreen(
    mainNavController: NavHostController,
    onLogout: () -> Unit
) {
    // NavController ini hanya untuk navigasi internal dashboard (Eksplor, Profil, dll)
    val dashboardNavController = rememberNavController()

    Scaffold(
        containerColor = Color(0xFFF7F7F7),
        bottomBar = {
            // --- PERBAIKAN KUNCI 1: Teruskan KEDUA NavController ---
            AppBottomNavigationBar(
                navController = dashboardNavController,       // Untuk navigasi item bar
                mainNavController = mainNavController,      // Untuk navigasi FAB (ke keranjang/transaksi)
                onItemClick = { screen ->
                    dashboardNavController.navigate(screen.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(dashboardNavController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                }
            )
        },
        content = { innerPadding ->
            DashboardNavGraph(
                navController = dashboardNavController,
                modifier = Modifier.padding(innerPadding),
                onLogout = onLogout
            )
        }
    )
}

// --- PERBAIKAN KUNCI 2: HAPUS FUNGSI ExploreScreen PALSU DARI SINI ---
// Composable ExploreScreen sudah ada di filenya sendiri dan telah diimpor di atas.
// Tidak boleh ada dua definisi fungsi dengan nama yang sama.


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
        composable(BottomBarScreen.Eksplor.route) {
            // Sekarang ini akan memanggil ExploreScreen yang benar dari filenya sendiri.
            ExploreScreen(modifier = Modifier.fillMaxSize())
        }
        composable(BottomBarScreen.Pencarian.route) {
            // Ganti Box placeholder dengan SearchingScreen yang sebenarnya
            SearchingScreen(
                onBack = {
                    // Kembali ke layar sebelumnya di dalam dashboard (biasanya Eksplor)
                    navController.popBackStack()
                }
            )
        }
        composable(BottomBarScreen.Riwayat.route) {
            HistoryScreen(
                onBack = {
                    // Kembali ke layar sebelumnya di dalam dashboard (biasanya Eksplor)
                    navController.popBackStack()
                }
            )
        }
        composable(BottomBarScreen.Profile.route) {
            ProfileScreen(onLogout = onLogout)
        }
    }
}

/**
 * Composable untuk isi dari Halaman Profile, termasuk tombol Logout.
 */

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen(
        mainNavController = rememberNavController(),
        onLogout = {}
    )
}
