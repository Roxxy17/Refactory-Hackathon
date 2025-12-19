package com.example.kalanacommerce.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kalanacommerce.front.components.AppBottomNavigationBar
import com.example.kalanacommerce.front.dashboard.ExploreScreen
import com.example.kalanacommerce.front.dashboard.HistoryScreen
import com.example.kalanacommerce.front.screen.dashboard.profile.ProfileScreen
import com.example.kalanacommerce.front.dashboard.SearchingScreen

/**
 * Layar utama yang menampung bottom navigation.
 * --- PERBAIKAN PARAMETER ---
 * Menerima semua parameter navigasi dan state dari AppNavGraph
 */
@Composable
fun DashboardNavigation(
    mainNavController: NavHostController,
    isLoggedIn: Boolean,
    onAuthAction: () -> Unit
) {
    val dashboardNavController = rememberNavController()

    Scaffold(
        containerColor = Color(0xFFF7F7F7),
        bottomBar = {
            AppBottomNavigationBar(
                navController = dashboardNavController,
                mainNavController = mainNavController,
                onItemClick = { screen ->
                    dashboardNavController.navigate(screen.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(dashboardNavController.graph.startDestinationId) { saveState = true }
                    }
                }
            )
        },
        content = { innerPadding ->
            // Teruskan semua parameter ke DashboardNavGraph
            DashboardNavGraph(
                modifier = Modifier.padding(innerPadding),
                dashboardNavController = dashboardNavController,
                mainNavController = mainNavController,
                isLoggedIn = isLoggedIn,
                onAuthAction = onAuthAction
            )
        }
    )
}

/**
 * NavHost untuk halaman-halaman yang diakses dari Bottom Navigation Bar.
 */
@Composable
fun DashboardNavGraph(
    modifier: Modifier,
    dashboardNavController: NavHostController,
    mainNavController: NavHostController,
    isLoggedIn: Boolean,
    onAuthAction: () -> Unit
) {
    NavHost(
        navController = dashboardNavController,
        startDestination = BottomBarScreen.Eksplor.route,
        modifier = modifier
    ) {
        composable(BottomBarScreen.Eksplor.route) {
            ExploreScreen(modifier = Modifier.fillMaxSize())
        }
        composable(BottomBarScreen.Pencarian.route) {
            SearchingScreen(onBack = { dashboardNavController.popBackStack() })
        }
        composable(BottomBarScreen.Riwayat.route) {
            HistoryScreen(onBack = { dashboardNavController.popBackStack() })
        }
        composable(BottomBarScreen.Profile.route) {
            // --- PERBAIKAN UTAMA ---
            // Teruskan parameter yang relevan ke ProfileScreen
            ProfileScreen(
                isLoggedIn = isLoggedIn,
                onAuthAction = onAuthAction,
                onNavigateToEditProfile = { mainNavController.navigate(Screen.EditProfile.route) },
                onNavigateToAddress = { mainNavController.navigate(Screen.Address.route) },
                onNavigateToSettings = { mainNavController.navigate(Screen.Settings.route) }
            )
        }
    }
}