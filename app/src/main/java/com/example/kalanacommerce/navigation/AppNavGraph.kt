package com.example.kalanacommerce.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kalanacommerce.data.local.SessionManager
import com.example.kalanacommerce.ui.dashboard.DashboardScreen
import com.example.kalanacommerce.ui.screen.TransactionScreen
import com.example.kalanacommerce.ui.dashboard.ChatScreen
import com.example.kalanacommerce.ui.dashboard.ProfileScreen
// Import yang ditambahkan
import com.example.kalanacommerce.ui.screen.auth.*
import com.example.kalanacommerce.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Rute ke Welcome / First Screen
        composable(route = Screen.Welcome.route) {
            FirstScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        // Rute ke Login Screen
        composable(route = Screen.Login.route) {
            // PERBAIKAN: Gunakan get() untuk mendapatkan SessionManager
            val sessionManager: SessionManager = get()
            val scope = rememberCoroutineScope()

            // Praktik terbaik: Gunakan ViewModel untuk logika login
            val authViewModel: AuthViewModel = koinViewModel()

            LoginScreen(
                // Teruskan ViewModel ke layar login
                viewModel = authViewModel,
                onSignInSuccess = {
                    // Arahkan ke Dashboard setelah login berhasil
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        // Kode BARU
        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {// Aksi jika user sudah punya akun dan klik "Login"
                    navController.navigate(Screen.Login.route) {
                        // Hapus RegisterScreen dari backstack
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                // PERUBAHAN DI SINI: Setelah registrasi berhasil (onContinue),
                // arahkan ke LoginScreen.
                onContinue = {
                    navController.navigate(Screen.Login.route) {
                        // Hapus RegisterScreen dari backstack agar user tidak bisa kembali
                        // ke halaman registrasi dengan menekan tombol back dari halaman login.
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = "chat_screen") {
            ChatScreen() // ChatScreen tidak butuh NavController untuk saat ini
        }


        // Rute ke Forgot Password Screen
        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- BLOK BARU YANG ANDA TAMBAHKAN ---
        composable(route = "transaction_screen") {
            TransactionScreen(
                navController = navController
            )
        }

        // --- BLOK BARU UNTUK PROFILE SCREEN ---
        // Ini ditambahkan sesuai permintaan Anda
        composable(BottomBarScreen.Profile.route) {
            val sessionManager: SessionManager = get()
            val scope = rememberCoroutineScope()
            // Panggil ProfileScreen yang baru dan teruskan onLogout
            ProfileScreen(onLogout = {
                scope.launch {
                    sessionManager.clearAuthData()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            })
        }

        // Rute ke Dashboard Screen
        composable(route = Screen.Dashboard.route) {
            val sessionManager: SessionManager = get()
            val scope = rememberCoroutineScope()

            DashboardScreen(
                // 1. TERUSKAN NavController UTAMA INI
                mainNavController = navController,
                onLogout = {
                    scope.launch {
                        sessionManager.clearAuthData()
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    }
                }
            )
        }
    }
}
