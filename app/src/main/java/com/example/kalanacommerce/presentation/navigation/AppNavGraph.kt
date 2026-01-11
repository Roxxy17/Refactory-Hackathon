// File: presentation/navigation/AppNavGraph.kt

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
import com.example.kalanacommerce.data.local.datastore.ThemeManager
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import com.example.kalanacommerce.presentation.screen.auth.forgotpassword.ForgotPasswordStepEmailScreen
import com.example.kalanacommerce.presentation.screen.auth.forgotpassword.ForgotPasswordStepOtpScreen
import com.example.kalanacommerce.presentation.screen.auth.login.LoginScreen
import com.example.kalanacommerce.presentation.screen.auth.login.SignInViewModel
import com.example.kalanacommerce.presentation.screen.auth.register.RegisterScreen
import com.example.kalanacommerce.presentation.screen.dashboard.DashboardScreen
import com.example.kalanacommerce.presentation.screen.dashboard.cart.CartScreen
import com.example.kalanacommerce.presentation.screen.dashboard.chat.ChatScreen
import com.example.kalanacommerce.presentation.screen.dashboard.detail.checkout.CheckoutScreen
import com.example.kalanacommerce.presentation.screen.dashboard.detail.payment.PaymentScreen
import com.example.kalanacommerce.presentation.screen.dashboard.history.HistoryScreen
import com.example.kalanacommerce.presentation.screen.dashboard.history.detail.DetailOrderPage
import com.example.kalanacommerce.presentation.screen.dashboard.detail.product.DetailProductPage
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.HelpCenterScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.TermsAndConditionsScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.addresspage.AddressFormScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.addresspage.AddressListScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.notification.SettingsPage
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.profilepage.EditProfilePage
import com.example.kalanacommerce.presentation.screen.dashboard.detail.store.DetailStorePage
import com.example.kalanacommerce.presentation.screen.start.GetStarted
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun RequireAuth(sessionManager: SessionManager, navController: NavHostController, content: @Composable () -> Unit) {
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = null)
    when (isLoggedIn) {
        true -> content()
        false -> LaunchedEffect(Unit) { navController.navigate(Graph.Auth) { popUpTo(0) { inclusive = true } } }
        null -> Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    }
}

@Composable
fun RedirectIfLoggedIn(sessionManager: SessionManager, navController: NavHostController, content: @Composable () -> Unit) {
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)
    LaunchedEffect(isLoggedIn) { if (isLoggedIn) navController.navigate(Screen.Dashboard.route) { popUpTo(0) { inclusive = true } } }
    if (!isLoggedIn) content()
}

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "root_splash"
) {
    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {

        // --- SPLASH / ROOT ---
        composable("root_splash") {
            val sessionManager: SessionManager = get()
            val isLoggedInState by sessionManager.isLoggedInFlow.collectAsState(initial = null)
            LaunchedEffect(isLoggedInState) {
                if (isLoggedInState != null) navController.navigate(Screen.Dashboard.route) { popUpTo(0) { inclusive = true } }
            }
        }

        // --- DASHBOARD ---
        composable(Screen.Dashboard.route) {
            DashboardScreen(mainNavController = navController)
        }

        // --- PUBLIC FEATURES ---
        composable(Screen.TermsAndConditions.route) {
            TermsAndConditionsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.HelpCenter.route) {
            HelpCenterScreen(onNavigateBack = { navController.popBackStack() })
        }

        // --- DETAIL SCREENS ---
        composable(
            route = Screen.DetailProduct.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)

            DetailProductPage(
                productId = productId,
                themeSetting = themeSetting,
                onBackClick = { navController.popBackStack() },
                onProductClick = { id -> navController.navigate(Screen.DetailProduct.createRoute(id)) },
                onStoreClick = { outletId -> navController.navigate(Screen.DetailStore.createRoute(outletId)) },
                onNavigateToCheckout = { itemIds -> navController.navigate(Screen.Checkout.createRoute(itemIds)) }
            )
        }

        composable(
            route = Screen.DetailStore.route,
            arguments = listOf(navArgument("outletId") { type = NavType.StringType })
        ) { backStackEntry ->
            val outletId = backStackEntry.arguments?.getString("outletId") ?: ""
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)

            DetailStorePage(
                outletId = outletId,
                themeSetting = themeSetting,
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId -> navController.navigate(Screen.DetailProduct.createRoute(productId)) }
            )
        }

        // --- TRANSACTION FLOW ---
        composable(Screen.Cart.route) {
            val sessionManager: SessionManager = get()
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)

            RequireAuth(sessionManager, navController) {
                CartScreen(
                    themeSetting = themeSetting,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNavigateToCheckout = { ids -> navController.navigate(Screen.Checkout.createRoute(ids)) },

                    // [PERBAIKAN] Navigasi ke Detail Product dan Store
                    onNavigateToDetailProduct = { productId ->
                        navController.navigate(Screen.DetailProduct.createRoute(productId))
                    },
                    onNavigateToStore = { outletId ->
                        navController.navigate(Screen.DetailStore.createRoute(outletId))
                    }
                )
            }
        }

        composable(
            route = Screen.Checkout.route,
            arguments = listOf(navArgument("itemIds") { type = NavType.StringType })
        ) { backStackEntry ->
            // ... (val itemIds, themeManager, dll tetap sama)
            val itemIds = backStackEntry.arguments?.getString("itemIds") ?: ""
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)

            CheckoutScreen(
                itemIdsString = itemIds,
                themeSetting = themeSetting,
                onBackClick = { navController.popBackStack() },

                // [PERBAIKAN] Menerima (url, id) dan memanggil createRoute dengan 2 argumen
                onNavigateToPayment = { paymentUrl, orderId ->
                    val encodedUrl = URLEncoder.encode(paymentUrl, StandardCharsets.UTF_8.toString())
                    navController.navigate(Screen.Payment.createRoute(encodedUrl, orderId))
                },
                onNavigateToAddress = {
                    navController.navigate(Screen.AddressList.route) // Gunakan rute AddressList yang benar
                }
            )
        }

        // --- HISTORY & CHAT ---
        composable(Screen.Transaction.route) {
            val sessionManager: SessionManager = get()
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)

            RequireAuth(sessionManager, navController) {
                HistoryScreen(
                    themeSetting = themeSetting,
                    onNavigateToDetail = { orderId -> navController.navigate(Screen.DetailOrder.createRoute(orderId)) }
                )
            }
        }

        composable(
            route = Screen.DetailOrder.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)

            DetailOrderPage(
                orderId = orderId,
                themeSetting = themeSetting,
                onBackClick = { navController.popBackStack() },

                // [PERBAIKAN] Menerima (url, id) dan memanggil createRoute dengan 2 argumen
                onNavigateToPayment = { snapUrl, id ->
                    val encodedUrl = URLEncoder.encode(snapUrl, StandardCharsets.UTF_8.toString())
                    navController.navigate(Screen.Payment.createRoute(encodedUrl, id))
                }
            )
        }

        composable(
            // Sesuaikan route string dengan yang ada di NavigationDestination.kt (payment_screen/{paymentUrl}/{orderId})
            route = Screen.Payment.route,
            arguments = listOf(
                navArgument("paymentUrl") { type = NavType.StringType }, // Pastikan nama argumen sama dengan di Screen.kt
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("paymentUrl") ?: "" // Ambil 'paymentUrl'
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""

            PaymentScreen(
                paymentUrl = url,
                orderId = orderId,
                onPaymentFinished = { id ->
                    navController.navigate(Screen.DetailOrder.createRoute(id)) {
                        popUpTo(Screen.Dashboard.route)
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Chat.route) {
            val sessionManager: SessionManager = get()
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)

            RequireAuth(sessionManager, navController) {
                ChatScreen(themeSetting = themeSetting, onBackClick = { navController.popBackStack() })
            }
        }

        // --- PROFILE SUB-SCREENS ---
        composable(Screen.EditProfile.route) {
            val sessionManager: SessionManager = get()
            RequireAuth(sessionManager, navController) { EditProfilePage(onBack = { navController.popBackStack() }) }
        }
        composable(Screen.Settings.route) {
            SettingsPage(onBack = { navController.popBackStack() })
        }

        // --- ADDRESS CRUD ---

        // [PERBAIKAN] Tambahkan Rute alias "address_list" agar Checkout tidak crash
        composable(Screen.AddressList.route) {
            val sessionManager: SessionManager = get()
            RequireAuth(sessionManager, navController) {
                AddressListScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToAdd = { navController.navigate(Screen.AddressCreate.route) },
                    onNavigateToEdit = { id -> navController.navigate(Screen.AddressEdit.createRoute(id)) }
                )
            }
        }

        composable(Screen.Address.route) {
            val sessionManager: SessionManager = get()
            RequireAuth(sessionManager, navController) {
                AddressListScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToAdd = { navController.navigate(Screen.AddressCreate.route) },
                    onNavigateToEdit = { id -> navController.navigate(Screen.AddressEdit.createRoute(id)) }
                )
            }
        }

        composable(Screen.AddressCreate.route) {
            val sessionManager: SessionManager = get()
            RequireAuth(sessionManager, navController) {
                AddressFormScreen(addressId = null, onBack = { navController.popBackStack() })
            }
        }

        composable(
            route = Screen.AddressEdit.route,
            arguments = listOf(navArgument("addressId") { type = NavType.StringType })
        ) { backStackEntry ->
            val addressId = backStackEntry.arguments?.getString("addressId")
            val sessionManager: SessionManager = get()
            RequireAuth(sessionManager, navController) {
                AddressFormScreen(addressId = addressId, onBack = { navController.popBackStack() })
            }
        }

        // --- AUTH GRAPH ---
        authGraph(navController, onNavigateToTerms = { navController.navigate(Screen.TermsAndConditions.route) })
    }
}

// --- AUTH GRAPH BUILDER ---
fun NavGraphBuilder.authGraph(navController: NavHostController, onNavigateToTerms: () -> Unit) {
    navigation(startDestination = Screen.Welcome.route, route = Graph.Auth) {
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
                onSignInSuccess = { navController.navigate(Screen.Dashboard.route) { popUpTo(0) { inclusive = true } } },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }
        composable(Screen.Register.route) {
            val sessionManager: SessionManager = get()
            RedirectIfLoggedIn(sessionManager, navController) {
                RegisterScreen(
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Register.route) { inclusive = true } } },
                    onContinue = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Register.route) { inclusive = true } } },
                    onNavigateToTerms = onNavigateToTerms,
                )
            }
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordStepEmailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToOtp = { email -> navController.navigate(Screen.ForgotPasswordOtp.createRoute(email)) }
            )
        }
        composable(
            route = Screen.ForgotPasswordOtp.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val sessionManager: SessionManager = get()
            ForgotPasswordStepOtpScreen(
                email = email,
                onNavigateBack = { navController.popBackStack() },
                onResetSuccess = {
                    kotlinx.coroutines.runBlocking { sessionManager.clearAuthData() }
                    navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                }
            )
        }
    }
}