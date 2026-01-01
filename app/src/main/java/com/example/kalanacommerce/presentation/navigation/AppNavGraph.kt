package com.example.kalanacommerce.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType // IMPORT WAJIB
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument // IMPORT WAJIB
import androidx.navigation.navigation
import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.presentation.screen.TransactionScreen
import com.example.kalanacommerce.presentation.screen.auth.forgotpassword.ForgotPasswordStepEmailScreen
import com.example.kalanacommerce.presentation.screen.auth.forgotpassword.ForgotPasswordStepOtpScreen
import com.example.kalanacommerce.presentation.screen.auth.login.LoginScreen
import com.example.kalanacommerce.presentation.screen.auth.login.SignInViewModel
import com.example.kalanacommerce.presentation.screen.auth.register.RegisterScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.TermsAndConditionsScreen
import com.example.kalanacommerce.presentation.screen.dashboard.ChatScreen
import com.example.kalanacommerce.presentation.screen.dashboard.DashboardScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.AddressPage
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.EditProfilePage
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.HelpCenterScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.SettingsPage
import com.example.kalanacommerce.presentation.screen.start.GetStarted
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
    startDestination: String = "root_splash"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        // --- SPLASH / ROOT LOGIC ---
        composable("root_splash") {
            val sessionManager: SessionManager = get()
            val isLoggedInState by sessionManager.isLoggedInFlow.collectAsState(initial = null)

            LaunchedEffect(isLoggedInState) {
                if (isLoggedInState != null) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            }
        }

        // --- Terms and Conditions ---
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

        composable(route = Screen.HelpCenter.route) {
            HelpCenterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // --- AUTH GRAPH ---
        authGraph(
            navController = navController,
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
            GetStarted(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Login.route) {
            val viewModel: SignInViewModel = koinViewModel()
            LoginScreen(
                viewModel = viewModel,
                onSignInSuccess = {
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

        // 1. Step Email
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordStepEmailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToOtp = { email ->
                    // Membawa argumen email ke screen OTP
                    navController.navigate("forgot_password_otp/$email")
                }
            )
        }

        // 2. Step OTP & Reset Password
        composable(
            route = "forgot_password_otp/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ForgotPasswordStepOtpScreen(
                email = email,
                onNavigateBack = { navController.popBackStack() },
                onResetSuccess = {
                    // Reset sukses, kembali ke Login dan hapus history
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Graph.Auth) { inclusive = true }
                    }
                }
            )
        }
    }
}