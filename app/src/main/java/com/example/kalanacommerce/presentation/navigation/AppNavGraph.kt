package com.example.kalanacommerce.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.presentation.screen.TransactionScreen
import com.example.kalanacommerce.presentation.screen.auth.forgotpassword.ForgotPasswordStepEmailScreen
import com.example.kalanacommerce.presentation.screen.auth.forgotpassword.ForgotPasswordStepOtpScreen
import com.example.kalanacommerce.presentation.screen.auth.login.LoginScreen
import com.example.kalanacommerce.presentation.screen.auth.login.SignInViewModel
import com.example.kalanacommerce.presentation.screen.auth.register.RegisterScreen
import com.example.kalanacommerce.presentation.screen.dashboard.chat.ChatScreen
import com.example.kalanacommerce.presentation.screen.dashboard.DashboardScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.HelpCenterScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.TermsAndConditionsScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.addresspage.AddressFormScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.addresspage.AddressListScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.notification.SettingsPage
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.profilepage.EditProfilePage
import com.example.kalanacommerce.presentation.screen.start.GetStarted
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import com.example.kalanacommerce.data.local.datastore.ThemeManager
import com.example.kalanacommerce.data.local.datastore.ThemeSetting

// 1. MIDDLEWARE: Satpam untuk User (Protected Routes)
@Composable
fun RequireAuth(
    sessionManager: SessionManager,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = null)

    when (isLoggedIn) {
        true -> content()
        false -> {
            LaunchedEffect(Unit) {
                navController.navigate(Graph.Auth) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
        null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

// 2. MIDDLEWARE: Satpam untuk Guest (Redirect if Logged In)
@Composable
fun RedirectIfLoggedIn(
    sessionManager: SessionManager,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    if (!isLoggedIn) {
        content()
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
        // --- SPLASH / ROOT ---
        composable("root_splash") {
            val sessionManager: SessionManager = get()
            val isLoggedInState by sessionManager.isLoggedInFlow.collectAsState(initial = null)

            LaunchedEffect(isLoggedInState) {
                if (isLoggedInState != null) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }

        // --- DASHBOARD ---
        composable(Screen.Dashboard.route) {
            DashboardScreen(mainNavController = navController)
        }

        // --- FITUR UMUM ---
        composable(route = Screen.TermsAndConditions.route) {
            TermsAndConditionsScreen(onBack = { navController.popBackStack() })
        }

        composable(route = Screen.HelpCenter.route) {
            HelpCenterScreen(onNavigateBack = { navController.popBackStack() })
        }

        // --- PROTECTED ROUTES (Fitur User) ---

        // Transaction & Chat
        composable(route = "transaction_screen") {
            val sessionManager: SessionManager = get()
            RequireAuth(sessionManager, navController) {
                TransactionScreen(navController = navController)
            }
        }

        composable(route = "chat_screen") {
            val sessionManager: SessionManager = get()

            // [FIX] Ambil ThemeManager dan collect state-nya
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)

            RequireAuth(sessionManager, navController) {
                ChatScreen(
                    themeSetting = themeSetting, // Teruskan ke ChatScreen
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Profile & Settings
        composable(route = Screen.EditProfile.route) {
            val sessionManager: SessionManager = get()
            RequireAuth(sessionManager, navController) { EditProfilePage(onBack = { navController.popBackStack() }) }
        }
        composable(route = Screen.Settings.route) {
            SettingsPage(onBack = { navController.popBackStack() })
        }

        // --- FITUR ALAMAT (CRUD) ---

        // 1. LIST ALAMAT
        composable(route = Screen.Address.route) {
            val sessionManager: SessionManager = get()
            RequireAuth(sessionManager, navController) {
                AddressListScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToAdd = {
                        navController.navigate("address_create")
                    },
                    onNavigateToEdit = { addressId ->
                        navController.navigate("address_edit/$addressId")
                    }
                )
            }
        }

        // 2. TAMBAH ALAMAT BARU
        composable(route = "address_create") {
            val sessionManager: SessionManager = get()
            RequireAuth(sessionManager, navController) {
                AddressFormScreen(
                    addressId = null, // ID Null = Mode Tambah
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // 3. EDIT ALAMAT
        composable(
            route = "address_edit/{addressId}",
            arguments = listOf(navArgument("addressId") { type = NavType.StringType })
        ) { backStackEntry ->
            val addressId = backStackEntry.arguments?.getString("addressId")
            val sessionManager: SessionManager = get()

            RequireAuth(sessionManager, navController) {
                AddressFormScreen(
                    addressId = addressId, // ID Ada = Mode Edit
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // --- AUTH GRAPH ---
        authGraph(navController, onNavigateToTerms = { navController.navigate(Screen.TermsAndConditions.route) })
    }
}

fun NavGraphBuilder.authGraph(navController: NavHostController, onNavigateToTerms: () -> Unit) {
    navigation(
        startDestination = Screen.Welcome.route,
        route = Graph.Auth
    ) {
        composable(Screen.Welcome.route) {
            val sessionManager: SessionManager = get()
            RedirectIfLoggedIn(sessionManager, navController) {
                GetStarted(
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }
        }

        composable(Screen.Login.route) {
            val viewModel: SignInViewModel = koinViewModel()
            LoginScreen(
                viewModel = viewModel,
                onSignInSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        composable(route = Screen.Register.route) {
            val sessionManager: SessionManager = get()
            RedirectIfLoggedIn(sessionManager, navController) {
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
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordStepEmailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToOtp = { email -> navController.navigate("forgot_password_otp/$email") }
            )
        }

        composable(
            route = "forgot_password_otp/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val sessionManager: SessionManager = get()

            ForgotPasswordStepOtpScreen(
                email = email,
                onNavigateBack = { navController.popBackStack() },
                onResetSuccess = {
                    kotlinx.coroutines.runBlocking { sessionManager.clearAuthData() }
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}