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
import com.example.kalanacommerce.ui.screen.auth.*
import com.example.kalanacommerce.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    // startDestination akan menentukan layar mana yang pertama kali ditampilkan
    startDestination: String
) {
    // HANYA ADA SATU NAVHOST DI SINI
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


        // Rute ke Forgot Password Screen
        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Rute ke Dashboard Screen
        composable(route = Screen.Dashboard.route) {
            // PERBAIKAN DI SINI: Gunakan get() bukan koinViewModel()
            val sessionManager: SessionManager = get()
            val scope = rememberCoroutineScope()

            DashboardScreen(
                onLogout = {
                    scope.launch {
                        sessionManager.clearAuthData()
                        // Navigasi kembali ke Welcome dan hapus semua history
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
