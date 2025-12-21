package com.example.kalanacommerce.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kalanacommerce.back.data.local.datastore.SessionManager
import com.example.kalanacommerce.front.components.AppBottomNavigationBar
import com.example.kalanacommerce.front.dashboard.ExploreScreen
import com.example.kalanacommerce.front.dashboard.HistoryScreen
import com.example.kalanacommerce.front.screen.dashboard.profile.ProfileScreen
import com.example.kalanacommerce.front.dashboard.SearchingScreen
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
    ) { innerPadding ->
        DashboardNavGraph(
            modifier = Modifier.padding(innerPadding),
            dashboardNavController = dashboardNavController,
            mainNavController = mainNavController,
            isLoggedIn = isLoggedIn,

            // LOGIC PENTING DI SINI:
            onAuthAction = {
                if (isLoggedIn) {
                    // JIKA MEMBER KLIK LOGOUT:
                    scope.launch {
                        sessionManager.clearAuthData() // 1. Hapus Sesi
                        // 2. User tetap di Dashboard, tapi UI Profile otomatis berubah jadi Guest
                        // Opsional: pindah ke Home biar fresh
                        dashboardNavController.navigate(BottomBarScreen.Eksplor.route)
                    }
                } else {
                    // JIKA GUEST KLIK LOGIN:
                    // Arahkan ke Navigasi Auth (Login/Register)
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
            // ProfileScreen sudah pintar, dia akan berubah tampilan sendiri
            // berdasarkan data dari ViewModel (User ada atau null).
            ProfileScreen(
                onAuthAction = onAuthAction, // Memanggil logic di atas
                onNavigateToEditProfile = {
                    // Navigasi ke Edit Profile (akan dicek RequireAuth di AppNavGraph)
                    mainNavController.navigate(Screen.EditProfile.route)
                },
                onNavigateToAddress = { mainNavController.navigate(Screen.Address.route) },
                onNavigateToSettings = { mainNavController.navigate(Screen.Settings.route) }
            )
        }
    }
}