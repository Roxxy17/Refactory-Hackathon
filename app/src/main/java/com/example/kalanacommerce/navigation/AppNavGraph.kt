package com.example.kalanacommerce.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kalanacommerce.back.data.local.datastore.SessionManager
import com.example.kalanacommerce.front.dashboard.DashboardScreen
import com.example.kalanacommerce.front.screen.TransactionScreen
import com.example.kalanacommerce.front.dashboard.ChatScreen
// Import Composable untuk halaman-halaman profil baru (asumsi sudah Anda buat)
import com.example.kalanacommerce.front.dashboard.Profile.EditProfilePage
import com.example.kalanacommerce.front.dashboard.Profile.AddressPage
import com.example.kalanacommerce.front.dashboard.Profile.SettingsPage
import com.example.kalanacommerce.front.screen.start.FirstScreen
import com.example.kalanacommerce.front.screen.start.ForgotPasswordScreen
import com.example.kalanacommerce.front.screen.auth.login.LoginScreen
import com.example.kalanacommerce.front.screen.auth.register.RegisterScreen
import com.example.kalanacommerce.front.screen.auth.login.SignInViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String
) {
    val sessionManager: SessionManager = get()
    val scope = rememberCoroutineScope()

    // Fungsi helper untuk logout dan navigasi ke Welcome
    val onLogout: () -> Unit = {
        scope.launch {
            sessionManager.clearAuthData()
            navController.navigate(Screen.Welcome.route) {
                // Hapus seluruh backstack
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    // Fungsi helper untuk navigasi keluar dari Dashboard
    val navigateTo: (Screen) -> Unit = { screen ->
        navController.navigate(screen.route)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        // --- AUTH FLOW ---

        composable(route = Screen.Welcome.route) {
            FirstScreen(
                onNavigateToLogin = { navigateTo(Screen.Login) },
                onNavigateToRegister = { navigateTo(Screen.Register) }
            )
        }

        composable(route = Screen.Login.route) {
            val authViewModel: SignInViewModel = koinViewModel()
            LoginScreen(
                viewModel = authViewModel,
                onSignInSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navigateTo(Screen.Register) },
                onNavigateToForgotPassword = { navigateTo(Screen.ForgotPassword) }
            )
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onContinue = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- CHAT & TRANSAKSI (Destinasi Langsung) ---

        composable(route = "chat_screen") {
            ChatScreen()
        }

        composable(route = "transaction_screen") {

            TransactionScreen(

                navController = navController

            )
        }
        // Ganti placeholder dengan halaman EditProfilePage yang sebenarnya
        composable(route = Screen.EditProfile.route) {
            EditProfilePage(onBack = { navController.popBackStack() })
        }

        // Ganti placeholder dengan halaman AddressPage yang sebenarnya
        composable(route = Screen.Address.route) {
            AddressPage(onBack = { navController.popBackStack() })
        }

        // Ganti placeholder dengan halaman SettingsPage yang sebenarnya
        composable(route = Screen.Settings.route) {
            SettingsPage(onBack = { navController.popBackStack() })
        }

        // --- DESTINASI BARU DARI PROFILE SCREEN (Sub-screens) ---


        // --- DASHBOARD (NESTED NAVIGATION HOST) ---

        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                // NavController utama disalurkan ke Dashboard untuk navigasi keluar (ke Profile sub-screens atau Logout)
                mainNavController = navController,
                onLogout = onLogout
            )
        }
    }
}