package com.example.kalanacommerce.presentation.screen.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
    // 1. Setup Controller & Session (Sama seperti file lama)
    val dashboardNavController = rememberNavController()
    val sessionManager: SessionManager = get()
    val scope = rememberCoroutineScope()

    // Pantau status login
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)

    Box(modifier = Modifier.fillMaxSize()) {

        // 2. AREA KONTEN (Layer Belakang)
        Scaffold(
            containerColor = Color.Transparent,
            // PENTING: Matikan insets agar konten tembus ke belakang status bar (seperti file lama)
            contentWindowInsets = WindowInsets(0.dp)
        ) { paddingValues ->
            // Kita abaikan paddingValues dari Scaffold ini agar konten full sampai bawah

            NavHost(
                navController = dashboardNavController,
                startDestination = BottomBarScreen.Eksplor.route,
                modifier = Modifier.fillMaxSize()
            ) {
                // --- TAB 1: HOME ---
                composable(BottomBarScreen.Eksplor.route) {
                    HomeScreen(modifier = Modifier.fillMaxSize())
                }

                // --- TAB 2: PENCARIAN (Sesuai file lamamu) ---
                composable(BottomBarScreen.Pencarian.route) {
                    SearchingScreen(onBack = { dashboardNavController.popBackStack() })
                }

                // --- TAB 3: RIWAYAT (Sesuai file lamamu) ---
                composable(BottomBarScreen.Riwayat.route) {
                    HistoryScreen(onBack = { dashboardNavController.popBackStack() })
                }

                // --- TAB 4: PROFILE ---
                composable(BottomBarScreen.Profile.route) {
                    ProfileScreen(
                        // Logic Auth Action (Sama persis dengan file lama)
                        onAuthAction = {
                            if (isLoggedIn) {
                                // LOGOUT MEMBER
                                scope.launch {
                                    sessionManager.clearAuthData()
                                    // Reset ke home setelah logout
                                    dashboardNavController.navigate(BottomBarScreen.Eksplor.route) {
                                        popUpTo(BottomBarScreen.Eksplor.route) { inclusive = true }
                                    }
                                }
                            } else {
                                // LOGIN GUEST
                                mainNavController.navigate(Graph.Auth)
                            }
                        },
                        // Navigasi Menu Profil (Sama persis)
                        onNavigateToEditProfile = { mainNavController.navigate(Screen.EditProfile.route) },
                        onNavigateToAddress = { mainNavController.navigate(Screen.Address.route) },
                        onNavigateToSettings = { mainNavController.navigate(Screen.Settings.route) },
                        onNavigateToTermsAndConditions = { mainNavController.navigate(Screen.TermsAndConditions.route) }
                    )
                }
            }
        }

        // 3. BOTTOM BAR (Overlay / Layer Atas)
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