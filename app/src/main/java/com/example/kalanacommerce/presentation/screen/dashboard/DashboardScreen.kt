package com.example.kalanacommerce.presentation.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding // <-- Tambahkan import ini
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.presentation.components.AppBottomNavigationBar
import com.example.kalanacommerce.presentation.navigation.BottomBarScreen
import com.example.kalanacommerce.presentation.navigation.Graph
import com.example.kalanacommerce.presentation.navigation.Screen
import com.example.kalanacommerce.presentation.screen.dashboard.home.HomeScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.ProfileScreen
// Import screen milikmu yang sudah ada
import com.example.kalanacommerce.presentation.screen.dashboard.search.SearchingScreen

import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
fun DashboardScreen(
    mainNavController: NavHostController
) {
    val dashboardNavController = rememberNavController()
    val sessionManager: SessionManager = get()
    val scope = rememberCoroutineScope()
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            // --- PERBAIKAN 1: Jadikan Scaffold UTAMA memiliki warna background yang benar ---
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0.dp)
        ) { paddingValues ->
            // --- PERBAIKAN 2: Terapkan padding ke NavHost ---
            // Ini akan mendorong konten NavHost ke atas, menjauh dari area Bottom Bar.
            NavHost(
                navController = dashboardNavController,
                startDestination = BottomBarScreen.Eksplor.route,
                // Terapkan padding yang diberikan oleh Scaffold
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // --- TAB 1: HOME ---
                composable(BottomBarScreen.Eksplor.route) {
                    HomeScreen(modifier = Modifier.fillMaxSize())
                }
                // --- TAB 2: PENCARIAN ---
                composable(BottomBarScreen.Pencarian.route) {
                    SearchingScreen(onBack = { dashboardNavController.popBackStack() })
                }
                // --- TAB 3: RIWAYAT ---
                composable(BottomBarScreen.Riwayat.route) {
                    HistoryScreen(onBack = { dashboardNavController.popBackStack() })
                }
                // --- TAB 4: PROFILE ---
                composable(BottomBarScreen.Profile.route) {
                    ProfileScreen(
                        onAuthAction = {
                            if (isLoggedIn) {
                                scope.launch {
                                    sessionManager.clearAuthData() // Menghapus token & set isLoggedIn = false

                                    // Navigasi keluar dari Dashboard ke Root
                                    mainNavController.navigate("root_splash") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            } else {
                                mainNavController.navigate(Graph.Auth)
                            }
                        },
                        onNavigateToEditProfile = { mainNavController.navigate(Screen.EditProfile.route) },
                        onNavigateToAddress = { mainNavController.navigate(Screen.Address.route) },
                        onNavigateToSettings = { mainNavController.navigate(Screen.Settings.route) },
                        onNavigateToTermsAndConditions = { mainNavController.navigate(Screen.TermsAndConditions.route) },
                        onNavigateToForgotPassword = {
                            mainNavController.navigate(Screen.ForgotPassword.route)
                        },
                        onNavigateToHelpCenter = { mainNavController.navigate(Screen.HelpCenter.route)}
                    )
                }
            }
        }

        // 3. BOTTOM BAR (Overlay / Layer Atas)
        // Ditempatkan di luar Scaffold agar bisa menjadi overlay transparan
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            AppBottomNavigationBar(
                navController = dashboardNavController,
                mainNavController = mainNavController,
                onItemClick = { screen ->
                    dashboardNavController.navigate(screen.route) {
                        popUpTo(dashboardNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
