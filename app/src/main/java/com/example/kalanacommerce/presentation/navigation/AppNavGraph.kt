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
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.TermsAndConditionsScreen
import com.example.kalanacommerce.presentation.screen.dashboard.ChatScreen
import com.example.kalanacommerce.presentation.screen.dashboard.DashboardScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.addresspage.AddressPage
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.profilepage.EditProfilePage
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.HelpCenterScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.notification.SettingsPage
import com.example.kalanacommerce.presentation.screen.start.GetStarted
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel

// 1. MIDDLEWARE: Satpam untuk User (Protected Routes)
@Composable
fun RequireAuth(
    sessionManager: SessionManager,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    // 1. UBAH INITIAL JADI NULL (State: Unknown/Loading)
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = null)

    when (isLoggedIn) {
        true -> {
            // Status: SUDAH LOGIN -> Boleh Masuk
            content()
        }
        false -> {
            // Status: BELUM LOGIN -> Tendang ke Halaman Auth
            LaunchedEffect(Unit) {
                navController.navigate(Graph.Auth) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
        null -> {
            // Status: LOADING (Sedang Cek DataStore) -> Tampilkan Loading Putih
            // Ini mencegah user ditendang padahal data belum siap
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

// 2. MIDDLEWARE BARU: Satpam untuk Guest (Redirect if Logged In)
// Ini yang akan memperbaiki masalah kamu terpental ke Login padahal masih ada session
@Composable
fun RedirectIfLoggedIn(
    sessionManager: SessionManager,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            // Jika sudah login tapi coba buka halaman Login/Register -> Lempar ke Dashboard
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
                // Tunggu sampai status login terbaca (tidak null)
                if (isLoggedInState != null) {
                    // Logikanya UBAH DISINI:
                    // Mau dia User (true) atau Guest (false), SEMUANYA boleh masuk Dashboard.
                    // Nanti Dashboard sendiri yang akan memilah tampilannya.
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(0) { inclusive = true } // Hapus Splash dari history
                    }
                }
            }
        }

        // --- DASHBOARD ---
        composable(Screen.Dashboard.route) {
            DashboardScreen(mainNavController = navController)
        }

        // --- FITUR UMUM (Bisa diakses siapa saja) ---
        composable(route = Screen.TermsAndConditions.route) {
            TermsAndConditionsScreen(onBack = { navController.popBackStack() })
        }

        composable(route = Screen.HelpCenter.route) {
            HelpCenterScreen(onNavigateBack = { navController.popBackStack() })
        }

        // --- PROTECTED ROUTES (Hanya User Login) ---
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

        // --- AUTH GRAPH ---
        authGraph(navController, onNavigateToTerms = { navController.navigate(Screen.TermsAndConditions.route) })
    }
}

fun NavGraphBuilder.authGraph(navController: NavHostController, onNavigateToTerms: () -> Unit) {
    navigation(
        startDestination = Screen.Welcome.route,
        route = Graph.Auth
    ) {
        // 1. Welcome (Hanya Guest)
        composable(Screen.Welcome.route) {
            val sessionManager: SessionManager = get()
            RedirectIfLoggedIn(sessionManager, navController) {
                GetStarted(
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }
        }

        // 2. Login (Hanya Guest)
        composable(Screen.Login.route) {
            val sessionManager: SessionManager = get()
            val viewModel: SignInViewModel = koinViewModel()

            LoginScreen(
                viewModel = viewModel,
                onSignInSuccess = {
                    // Navigasi manual dijalankan setelah Toast selesai (delay 1.5s di LoginScreen)
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )

        }

        // 3. Register (Hanya Guest)
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

        // --- FORGOT PASSWORD FLOW ---
        // Catatan: Screen ini TIDAK dibungkus RedirectIfLoggedIn agar bisa diakses dari Profile

        // Step 1: Email
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordStepEmailScreen(
                onNavigateBack = {
                    // Logika Back: Cukup popBackStack.
                    // Jika dari Login -> balik ke Login. Jika dari Profile -> balik ke Profile.
                    navController.popBackStack()
                },
                onNavigateToOtp = { email ->
                    navController.navigate("forgot_password_otp/$email")
                }
            )
        }

        // Step 2: OTP
        composable(
            route = "forgot_password_otp/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val sessionManager: SessionManager = get() // Perlu ini untuk Logout paksa

            ForgotPasswordStepOtpScreen(
                email = email,
                onNavigateBack = { navController.popBackStack() },
                onResetSuccess = {
                    // 1. Clear Data Session (Logout)
                    kotlinx.coroutines.runBlocking {
                        sessionManager.clearAuthData()
                    }
                    // 2. Arahkan ke Login Screen
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true } // Hapus semua history
                    }
                }
            )
        }
    }
}