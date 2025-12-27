package com.example.kalanacommerce.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.presentation.screen.TransactionScreen
import com.example.kalanacommerce.presentation.screen.auth.forgotpassword.ForgotPasswordStepEmailScreen
import com.example.kalanacommerce.presentation.screen.auth.login.LoginScreen
import com.example.kalanacommerce.presentation.screen.auth.login.SignInViewModel
import com.example.kalanacommerce.presentation.screen.auth.register.RegisterScreen
import com.example.kalanacommerce.presentation.screen.auth.terms.TermsAndConditionsScreen
import com.example.kalanacommerce.presentation.screen.dashboard.ChatScreen
import com.example.kalanacommerce.presentation.screen.dashboard.DashboardScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.AddressPage
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.EditProfilePage
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.SettingsPage
import com.example.kalanacommerce.presentation.screen.start.FirstScreen
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

// Middleware Auth: Tetap sama (tidak berubah)
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
            navController.navigate(Graph.Auth)
        }
    }
}

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    // UBAH 1: Start Destination diarahkan ke "root_splash" dulu
    startDestination: String = "root_splash"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        // --- UBAH 2: TAMBAHKAN SPLASH / ROOT LOGIC DI SINI ---
        composable("root_splash") {
            val sessionManager: SessionManager = get()
            // Kita butuh 'initial = null' untuk membedakan belum loading vs false
            val isLoggedInState by sessionManager.isLoggedInFlow.collectAsState(initial = null)

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                // Tampilkan loading spinner selagi membaca DataStore
                CircularProgressIndicator()
            }

            LaunchedEffect(isLoggedInState) {
                when (isLoggedInState) {
                    true -> {
                        // Jika TRUE (Remember Me aktif): Langsung ke Dashboard
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo("root_splash") { inclusive = true }
                        }
                    }
                    false -> {
                        // Jika FALSE:
                        // Opsi A: Langsung ke Welcome/Login (jika aplikasi wajib login)
                        // navController.navigate(Graph.Auth) { popUpTo("root_splash") { inclusive = true } }

                        // Opsi B: Tetap ke Dashboard (sebagai Guest) -> Sesuai kode Anda sebelumnya
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo("root_splash") { inclusive = true }
                        }
                    }
                    null -> {
                        // Sedang loading (do nothing)
                    }
                }
            }
        }

        // terms and condition
        composable(route = Screen.TermsAndConditions.route) {
            TermsAndConditionsScreen(onBack = { navController.popBackStack() })
        }

        // --- DASHBOARD ---
        composable(Screen.Dashboard.route) {
            DashboardScreen(mainNavController = navController)
        }

        // --- PROTECTED ROUTES ---
        composable(route = "transaction_screen") {
            val sessionManager: SessionManager = get()
            RequireAuth(sessionManager, navController) {
                TransactionScreen(navController = navController)
            }
        }

        // ... (Route Chat, EditProfile, Address, Settings, Terms sama seperti sebelumnya) ...
        composable(route = "chat_screen") {
            val sessionManager: SessionManager = get()
            RequireAuth(sessionManager, navController) { ChatScreen() }
        }
        composable(route = Screen.EditProfile.route) {
            val sessionManager: SessionManager = get()
            RequireAuth(sessionManager, navController) { EditProfilePage(onBack = { navController.popBackStack() }) }
        }
        composable(route = Screen.Address.route) {
            val sessionManager: SessionManager = get()
            RequireAuth(sessionManager, navController) { AddressPage(onBack = { navController.popBackStack() }) }
        }
        composable(route = Screen.Settings.route) {
            SettingsPage(onBack = { navController.popBackStack() })
        }

        authGraph(
            navController = navController,
            // Teruskan lambda untuk navigasi ke Terms and Conditions
            onNavigateToTerms = {
                navController.navigate(Screen.TermsAndConditions.route)
            }
        )
    }
}

fun NavGraphBuilder.authGraph(navController: NavHostController, onNavigateToTerms: () -> Unit = {}) {
    navigation(
        startDestination = Screen.Welcome.route,
        route = Graph.Auth
    ) {
        composable(Screen.Welcome.route) {
            FirstScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Login.route) {
            val viewModel: SignInViewModel = koinViewModel()
            LoginScreen(
                viewModel = viewModel,
                onSignInSuccess = {
                    // SUKSES LOGIN: Hapus seluruh history Auth agar tombol back tidak balik ke login
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Graph.Auth) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
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
                },
                onNavigateToTerms = onNavigateToTerms,
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordStepEmailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}