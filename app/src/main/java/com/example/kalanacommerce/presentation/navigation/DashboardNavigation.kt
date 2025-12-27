package com.example.kalanacommerce.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets // PENTING
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp // PENTING
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.presentation.components.AppBottomNavigationBar
import com.example.kalanacommerce.presentation.screen.dashboard.home.ExploreScreen
import com.example.kalanacommerce.presentation.screen.dashboard.HistoryScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.ProfileScreen
import com.example.kalanacommerce.presentation.screen.dashboard.SearchingScreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
fun DashboardNavigation(
    mainNavController: NavHostController,
) {
    val dashboardNavController = rememberNavController()
    val sessionManager: SessionManager = get()
    val scope = rememberCoroutineScope()

    // Pantau status login untuk bottom bar atau logic lain jika perlu
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)

    Scaffold(
        // 1. Ubah warna container agar mengikuti Tema (Dark/Light)
        containerColor = MaterialTheme.colorScheme.background,

        // 2. PERBAIKAN UTAMA: Matikan insets default
        // Ini memaksa Scaffold untuk TIDAK memberikan padding putih/abu di balik status bar.
        // Konten akan digambar "Edge-to-Edge" (tembus ke belakang jam).
        contentWindowInsets = WindowInsets(0.dp),

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
    ) { innerPadding ->
        // innerPadding ini sekarang nilai top-nya adalah 0 (karena WindowInsets(0.dp))
        // Jadi ProfileScreen akan mulai dari koordinat y=0 (paling atas layar)
        DashboardNavGraph(
            modifier = Modifier.padding(innerPadding),
            dashboardNavController = dashboardNavController,
            mainNavController = mainNavController,
            isLoggedIn = isLoggedIn,

            // LOGIC AUTH ACTION
            onAuthAction = {
                if (isLoggedIn) {
                    // JIKA MEMBER KLIK LOGOUT:
                    scope.launch {
                        sessionManager.clearAuthData()
                        // User tetap di Dashboard, tapi UI Profile otomatis berubah jadi Guest
                        dashboardNavController.navigate(BottomBarScreen.Eksplor.route)
                    }
                } else {
                    // JIKA GUEST KLIK LOGIN:
                    mainNavController.navigate(Graph.Auth)
                }
            }
        )
    }
}

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
            ProfileScreen(
                onAuthAction = onAuthAction,
                onNavigateToEditProfile = {
                    mainNavController.navigate(Screen.EditProfile.route)
                },
                onNavigateToAddress = { mainNavController.navigate(Screen.Address.route) },
                onNavigateToSettings = { mainNavController.navigate(Screen.Settings.route) },
                onNavigateToTermsAndConditions = { mainNavController.navigate(Screen.TermsAndConditions.route) }
            )
        }

    }
}