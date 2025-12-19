package com.example.kalanacommerce.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.kalanacommerce.back.data.local.datastore.SessionManager
import com.example.kalanacommerce.front.dashboard.ChatScreen
import com.example.kalanacommerce.front.dashboard.Profile.AddressPage
import com.example.kalanacommerce.front.dashboard.Profile.EditProfilePage
import com.example.kalanacommerce.front.dashboard.Profile.SettingsPage
import com.example.kalanacommerce.front.screen.TransactionScreen
import com.example.kalanacommerce.front.screen.auth.forgotpassword.ForgotPasswordStepEmailScreen
import com.example.kalanacommerce.front.screen.auth.login.LoginScreen
import com.example.kalanacommerce.front.screen.auth.login.SignInViewModel
import com.example.kalanacommerce.front.screen.auth.register.RegisterScreen
import com.example.kalanacommerce.front.screen.start.FirstScreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

// "Middleware" atau "Penjaga" Otentikasi
@Composable
fun RequireAuth(
    sessionManager: SessionManager,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)

    if (isLoggedIn) {
        content() // Pengguna sudah login, tampilkan kontennya.
    } else {
        // Pengguna belum login, arahkan ke alur otentikasi.
        LaunchedEffect(key1 = Unit) {
            navController.navigate(Graph.Auth)
        }
    }
}

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val sessionManager: SessionManager = get()
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route, // SELALU MULAI DARI DASHBOARD
        modifier = modifier
    ) {

        // =====================================================================
        // === DEFINISI RUTE-RUTE UTAMA ========================================
        // =====================================================================

        // --- RUTE UTAMA: DASHBOARD (Pintu Masuk Aplikasi) ---
        composable(route = Screen.Dashboard.route) {
            // 1. Ambil status login di sini, sebagai sumber kebenaran.
            val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)

            // 2. Teruskan state dan event handler ke DashboardNavigation.
            DashboardNavigation(
                mainNavController = navController,
                isLoggedIn = isLoggedIn,
                onAuthAction = {
                    if (isLoggedIn) {
                        // Jika sudah login, lakukan logout
                        scope.launch {
                            sessionManager.clearAuthData()
                            // Tetap di dashboard setelah logout
                            navController.popBackStack(Screen.Dashboard.route, inclusive = false)
                        }
                    } else {
                        // Jika belum login, arahkan ke alur otentikasi
                        navController.navigate(Graph.Auth)
                    }
                }
            )
        }

        // --- RUTE-RUTE YANG DIPROTEKSI (Butuh Login) ---
        composable(route = "transaction_screen") { // Ganti dengan Screen.Transaction.route jika ada
            RequireAuth(sessionManager = sessionManager, navController = navController) {
                TransactionScreen(navController = navController)
            }
        }
        composable(route = "chat_screen") { // Ganti dengan Screen.Chat.route jika ada
            RequireAuth(sessionManager = sessionManager, navController = navController) {
                ChatScreen()
            }
        }
        composable(route = Screen.EditProfile.route) {
            RequireAuth(sessionManager = sessionManager, navController = navController) {
                EditProfilePage(onBack = { navController.popBackStack() })
            }
        }
        composable(route = Screen.Address.route) {
            RequireAuth(sessionManager = sessionManager, navController = navController) {
                AddressPage(onBack = { navController.popBackStack() })
            }
        }
        composable(route = Screen.Settings.route) {
            // Contoh SettingsPage yang mungkin perlu login
            RequireAuth(sessionManager = sessionManager, navController = navController) {
                // Asumsi SettingsPage hanya berisi pengaturan teknis
                // Jika ingin ada Login/Logout, logikanya harus seperti ProfileScreen
                SettingsPage(onBack = { navController.popBackStack() })
            }
        }


        // --- GRAFIK NAVIGASI OTENTIKASI ---
        authGraph(navController = navController)
    }
}

// Fungsi ekstensi untuk merapikan NavGraphBuilder
fun NavGraphBuilder.authGraph(navController: NavHostController) {
    // Definisikan nested graph baru
    navigation(
        startDestination = Screen.Welcome.route,
        route = Graph.Auth
    ) {
        composable(route = Screen.Welcome.route) {
            FirstScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(route = Screen.Login.route) {
            val authViewModel: SignInViewModel = koinViewModel()
            LoginScreen(
                viewModel = authViewModel,
                onSignInSuccess = {
                    // Setelah sukses login, tutup auth graph & kembali ke layar sebelumnya
                    navController.popBackStack(Graph.Auth, inclusive = true)
                },
                onNavigateToRegister = {
                    // Tukar layar Login dengan Register
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    // Tukar layar Register dengan Login
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onContinue = {
                    // Setelah sukses register, tutup auth graph & kembali ke layar sebelumnya
                    navController.popBackStack(Graph.Auth, inclusive = true)
                }
            )
        }

        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordStepEmailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
