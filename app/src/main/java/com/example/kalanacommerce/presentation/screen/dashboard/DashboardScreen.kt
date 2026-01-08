package com.example.kalanacommerce.presentation.screen.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.data.local.datastore.ThemeManager
import com.example.kalanacommerce.presentation.components.AppBottomNavigationBar
import com.example.kalanacommerce.presentation.navigation.BottomBarScreen
import com.example.kalanacommerce.presentation.navigation.Graph
import com.example.kalanacommerce.presentation.navigation.Screen
import com.example.kalanacommerce.presentation.screen.dashboard.explore.ExploreScreen
import com.example.kalanacommerce.presentation.screen.dashboard.home.HomeScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.ProfileScreen
// [PENTING] Pastikan import HistoryScreen ada
import com.example.kalanacommerce.presentation.screen.dashboard.history.HistoryScreen

import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
fun DashboardScreen(
    mainNavController: NavHostController
) {
    val dashboardNavController = rememberNavController()

    // Inject Manager
    val sessionManager: SessionManager = get()
    val themeManager: ThemeManager = get()

    val scope = rememberCoroutineScope()

    // Observasi status Login & Tema
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)
    val themeSetting by themeManager.themeSettingFlow.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0.dp)
        ) { paddingValues ->

            NavHost(
                navController = dashboardNavController,
                startDestination = BottomBarScreen.Eksplor.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // --- TAB 1: HOME ---
                composable(BottomBarScreen.Eksplor.route) {
                    HomeScreen(
                        themeSetting = themeSetting,
                        onProductClick = { productId ->
                            mainNavController.navigate("detail_product/$productId")
                        }
                    )
                }

                // --- TAB 2: PENCARIAN (EXPLORE) ---
                composable(BottomBarScreen.Pencarian.route) {
                    ExploreScreen(
                        themeSetting = themeSetting,
                        onBackClick = {
                            dashboardNavController.navigate(BottomBarScreen.Eksplor.route) {
                                popUpTo(dashboardNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onProductClick = { productId ->
                            mainNavController.navigate("detail_product/$productId")
                        }
                    )
                }

                // --- TAB 3: RIWAYAT (UPDATED) ---
                composable(BottomBarScreen.Riwayat.route) {
                    if (isLoggedIn) {
                        // Tampilkan History Screen jika User Login
                        HistoryScreen(
                            themeSetting = themeSetting,
                            onNavigateToDetail = { orderId ->
                                // Navigasi ke Detail Order (gunakan mainNavController untuk menutup bottom bar)
                                mainNavController.navigate("order_detail/$orderId")
                            }
                        )
                    } else {
                        // Tampilkan Halaman Login Required jika Guest
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Silakan login untuk melihat riwayat")
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { mainNavController.navigate(Graph.Auth) }) {
                                    Text("Masuk Sekarang")
                                }
                            }
                        }
                    }
                }

                // --- TAB 4: PROFILE ---
                composable(BottomBarScreen.Profile.route) {
                    ProfileScreen(
                        onAuthAction = {
                            if (isLoggedIn) {
                                scope.launch {
                                    sessionManager.clearAuthData()
                                    mainNavController.navigate(Screen.Dashboard.route) {
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
                        onNavigateToForgotPassword = { mainNavController.navigate(Screen.ForgotPassword.route) },
                        onNavigateToHelpCenter = { mainNavController.navigate(Screen.HelpCenter.route) }
                    )
                }
            }
        }

        // BOTTOM BAR (Overlay)
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