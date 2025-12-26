package com.example.kalanacommerce.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.kalanacommerce.front.screen.auth.terms.TermsAndConditionsScreen
import com.example.kalanacommerce.front.screen.start.FirstScreen
import org.koin.androidx.compose.koinViewModel

// Middleware Auth: Jika Guest mencoba masuk sini, lempar ke Login
@Composable
fun RequireAuth(
    sessionManager: SessionManager,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)

    if (isLoggedIn) {
        content()
    } else {
        LaunchedEffect(Unit) {
            // Arahkan ke Login, nanti setelah login balik lagi ke Dashboard
            navController.navigate(Graph.Auth)
        }
    }
}

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Dashboard.route // Default Dashboard
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        // --- DASHBOARD (Bisa Guest, Bisa Member) ---
        composable(Screen.Dashboard.route) {
            DashboardNavigation(
                mainNavController = navController
            )
        }

        // --- HALAMAN YANG BUTUH LOGIN (Protected) ---
        // Contoh: Transaksi hanya boleh jika login
        composable(route = "transaction_screen") {
            val sessionManager: SessionManager = org.koin.androidx.compose.get()
            RequireAuth(sessionManager, navController) {
                TransactionScreen(navController = navController)
            }
        }

        // Contoh: Chat hanya boleh jika login
        composable(route = "chat_screen") {
            val sessionManager: SessionManager = org.koin.androidx.compose.get()
            RequireAuth(sessionManager, navController) {
                ChatScreen()
            }
        }

        // Contoh: Edit Profile & Address harus login
        composable(route = Screen.EditProfile.route) {
            val sessionManager: SessionManager = org.koin.androidx.compose.get()
            RequireAuth(sessionManager, navController) {
                EditProfilePage(onBack = { navController.popBackStack() })
            }
        }
        composable(route = Screen.Address.route) {
            val sessionManager: SessionManager = org.koin.androidx.compose.get()
            RequireAuth(sessionManager, navController) {
                AddressPage(onBack = { navController.popBackStack() })
            }
        }

        // Settings mungkin boleh guest, tapi kita proteksi untuk contoh ini
        composable(route = Screen.Settings.route) {
            SettingsPage(onBack = { navController.popBackStack() })
        }

        // Terms & Conditions mungkin boleh guest, tapi kita proteksi untuk contoh ini
        composable(route = Screen.TermsAndConditions.route) {
            TermsAndConditionsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // --- AUTH GRAPH (Login, Register, dll) ---
        authGraph(navController)
    }
}

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.Welcome.route,
        route = Graph.Auth
    ) {
        // Halaman Welcome (Login / Register)
        composable(Screen.Welcome.route) {
            FirstScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        // Halaman Login
        composable(Screen.Login.route) {
            val viewModel: SignInViewModel = koinViewModel()
            LoginScreen(
                viewModel = viewModel,
                onSignInSuccess = {
                    // SUKSES LOGIN: Kembali ke Dashboard (sebagai Member)
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onContinue = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordStepEmailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}