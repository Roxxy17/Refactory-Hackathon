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
import com.example.kalanacommerce.presentation.screen.dashboard.detail.product.DetailProductPage
import com.example.kalanacommerce.presentation.screen.dashboard.detail.store.DetailStorePage
import com.example.kalanacommerce.presentation.screen.dashboard.detail.success.OrderSuccessScreen
import com.example.kalanacommerce.presentation.screen.dashboard.history.HistoryScreen
import com.example.kalanacommerce.presentation.screen.dashboard.history.detail.DetailOrderPage
import com.example.kalanacommerce.presentation.screen.dashboard.history.group.TransactionGroupScreen
import com.example.kalanacommerce.presentation.screen.dashboard.map.MapPickerScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.HelpCenterScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.TermsAndConditionsScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.addresspage.AddressFormScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.addresspage.AddressListScreen
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.notification.SettingsPage
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.profilepage.EditProfilePage
import com.example.kalanacommerce.presentation.screen.start.GetStarted
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// --- Helper Composable untuk Memaksa Login ---
@Composable
fun RequireAuth(
    sessionManager: SessionManager,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = null)
    when (isLoggedIn) {
        true -> content()
        false -> LaunchedEffect(Unit) {
            // Jika belum login, lempar ke Auth Graph
            navController.navigate(Graph.Auth) {
                // Opsional: Agar saat back dari login tidak stuck
                popUpTo(0) { inclusive = true }
            }
        }
        null -> Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }
    }
}

@Composable
fun RedirectIfLoggedIn(
    sessionManager: SessionManager,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) navController.navigate(Screen.Dashboard.route) {
            popUpTo(0) { inclusive = true }
        }
    }
    if (!isLoggedIn) content()
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
                if (isLoggedInState != null) navController.navigate(Screen.Dashboard.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }

        composable(
            route = Screen.MapPicker.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType; defaultValue = "0.0" },
                navArgument("long") { type = NavType.StringType; defaultValue = "0.0" }
            )
        ) { backStackEntry ->
            val latStr = backStackEntry.arguments?.getString("lat") ?: "0.0"
            val longStr = backStackEntry.arguments?.getString("long") ?: "0.0"

            MapPickerScreen(
                initialLat = latStr.toDoubleOrNull() ?: 0.0,
                initialLong = longStr.toDoubleOrNull() ?: 0.0,
                onBackClick = { navController.popBackStack() },
                onConfirmLocation = { lat, long ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("location_result", "$lat,$long")
                    navController.popBackStack()
                }
            )
        }

        // --- DASHBOARD ---
        composable(Screen.Dashboard.route) {
            DashboardScreen(mainNavController = navController)
        }

        // --- PUBLIC FEATURES ---
        composable(Screen.TermsAndConditions.route) {
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)
            TermsAndConditionsScreen(
                onBack = { navController.popBackStack() },
                themeSetting = themeSetting
            )
        }
        composable(Screen.HelpCenter.route) {
            HelpCenterScreen(onNavigateBack = { navController.popBackStack() })
        }

        // --- DETAIL SCREENS (PRODUCT) ---
        composable(
            route = Screen.DetailProduct.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)

            // [LOGIC PENTING] Ambil status login untuk cek tombol beli/keranjang
            val sessionManager: SessionManager = get()
            val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)

            DetailProductPage(
                productId = productId,
                themeSetting = themeSetting,
                onBackClick = { navController.popBackStack() },
                onProductClick = { id -> navController.navigate(Screen.DetailProduct.createRoute(id)) },
                onStoreClick = { outletId ->
                    navController.navigate(Screen.DetailStore.createRoute(outletId))
                },
                // [PROTEKSI 1] Saat mau Checkout dari Detail Produk
                onNavigateToCheckout = { itemIds ->
                    if (isLoggedIn) {
                        navController.navigate(Screen.Checkout.createRoute(itemIds))
                    } else {
                        // Jika belum login, lempar ke Login Page
                        navController.navigate(Graph.Auth)
                    }
                },
                // [PROTEKSI 2] Saat mau lihat Keranjang
                onNavigateToCart = {
                    if (isLoggedIn) {
                        navController.navigate(Screen.Cart.route)
                    } else {
                        navController.navigate(Graph.Auth)
                    }
                }
            )
        }

        composable(
            route = Screen.DetailStore.route,
            arguments = listOf(navArgument("outletId") { type = NavType.StringType })
        ) { backStackEntry ->
            val outletId = backStackEntry.arguments?.getString("outletId") ?: ""
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)

            // [LOGIC PENTING] Ambil status login
            val sessionManager: SessionManager = get()
            val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)

            DetailStorePage(
                outletId = outletId,
                themeSetting = themeSetting,
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate(Screen.DetailProduct.createRoute(productId))
                },
                // [PROTEKSI 3] Navigasi ke Keranjang dari Toko
                onNavigateToCart = {
                    if (isLoggedIn) {
                        navController.navigate(Screen.Cart.route)
                    } else {
                        navController.navigate(Graph.Auth)
                    }
                }
            )
        }

        // --- TRANSACTION FLOW ---

        // [PROTEKSI 4] Keranjang Belanja Wajib Login (Sesuai Request)
        composable(Screen.Cart.route) {
            val sessionManager: SessionManager = get()
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)
            val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)

            // Bungkus dengan RequireAuth agar tidak bisa diakses url-nya tanpa login
            RequireAuth(sessionManager, navController) {
                CartScreen(
                    isLoggedIn = isLoggedIn,
                    themeSetting = themeSetting,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToLogin = { navController.navigate(Graph.Auth) },
                    onNavigateToHome = {
                        navController.navigate(Screen.Dashboard.route) { popUpTo(0) }
                    },
                    onNavigateToCheckout = { ids ->
                        navController.navigate(Screen.Checkout.createRoute(ids))
                    },
                    onNavigateToDetailProduct = { productId ->
                        navController.navigate(Screen.DetailProduct.createRoute(productId))
                    },
                    onNavigateToStore = { outletId ->
                        navController.navigate(Screen.DetailStore.createRoute(outletId))
                    }
                )
            }
        }

        // [PROTEKSI 5] Checkout Wajib Login
        composable(
            route = Screen.Checkout.route,
            arguments = listOf(navArgument("itemIds") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemIds = backStackEntry.arguments?.getString("itemIds") ?: ""
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)
            val sessionManager: SessionManager = get()

            // Gunakan RequireAuth
            RequireAuth(sessionManager, navController) {
                CheckoutScreen(
                    itemIdsString = itemIds,
                    themeSetting = themeSetting,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToPayment = { paymentUrl, orderId, paymentGroupId ->
                        val encodedUrl = URLEncoder.encode(paymentUrl, StandardCharsets.UTF_8.toString())
                        navController.navigate(
                            Screen.Payment.createRoute(encodedUrl, orderId, paymentGroupId)
                        )
                    },
                    onNavigateToAddress = {
                        navController.navigate(Screen.AddressList.route)
                    }
                )
            }
        }

        // --- HISTORY & ORDER DETAIL ---
        composable(Screen.Transaction.route) {
            val sessionManager: SessionManager = get()
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)

            RequireAuth(sessionManager, navController) {
                HistoryScreen(
                    themeSetting = themeSetting,
                    onNavigateToDetail = { orderId ->
                        navController.navigate(Screen.DetailOrder.createRoute(orderId))
                    },
                    onNavigateToGroupDetail = { groupId ->
                        navController.navigate(Screen.TransactionGroupDetail.createRoute(groupId))
                    },
                    onNavigateToMaps = { orderId ->
                        navController.navigate(Screen.OrderSuccess.createRoute(orderId = orderId))
                    }
                )
            }
        }

        // [PROTEKSI 6] Detail Group Transaction Wajib Login
        composable(
            route = Screen.TransactionGroupDetail.route,
            arguments = listOf(navArgument("paymentGroupId") { type = NavType.StringType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("paymentGroupId") ?: ""
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)
            val sessionManager: SessionManager = get()

            RequireAuth(sessionManager, navController) {
                TransactionGroupScreen(
                    paymentGroupId = groupId,
                    themeSetting = themeSetting,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToOrderDetail = { orderId ->
                        navController.navigate(Screen.DetailOrder.createRoute(orderId))
                    },
                    onNavigateToMaps = { orderId, paymentGroupId ->
                        navController.navigate(
                            Screen.OrderSuccess.createRoute(
                                orderId = orderId,
                                paymentGroupId = paymentGroupId
                            )
                        )
                    }
                )
            }
        }

        // [PROTEKSI 7] Detail Order Wajib Login
        composable(
            route = Screen.DetailOrder.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)
            val sessionManager: SessionManager = get()

            RequireAuth(sessionManager, navController) {
                DetailOrderPage(
                    orderId = orderId,
                    themeSetting = themeSetting,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToPayment = { snapUrl, id, groupId ->
                        val encodedUrl = URLEncoder.encode(snapUrl, StandardCharsets.UTF_8.toString())
                        navController.navigate(Screen.Payment.createRoute(encodedUrl, id, groupId))
                    },
                    onNavigateToMaps = { id ->
                        navController.navigate(Screen.OrderSuccess.createRoute(orderId = id))
                    }
                )
            }
        }

        // [PROTEKSI 8] Payment Screen Wajib Login
        composable(
            route = Screen.Payment.route,
            arguments = listOf(
                navArgument("paymentUrl") { type = NavType.StringType },
                navArgument("orderId") { type = NavType.StringType },
                navArgument("paymentGroupId") {
                    type = NavType.StringType; nullable = true; defaultValue = null
                }
            )
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("paymentUrl") ?: ""
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val paymentGroupId = backStackEntry.arguments?.getString("paymentGroupId")
            val sessionManager: SessionManager = get()

            RequireAuth(sessionManager, navController) {
                PaymentScreen(
                    paymentUrl = url,
                    orderId = orderId,
                    onPaymentFinished = { _ ->
                        val targetRoute = Screen.OrderSuccess.createRoute(
                            orderId = if (paymentGroupId == null) orderId else null,
                            paymentGroupId = paymentGroupId
                        )

                        navController.navigate(targetRoute) {
                            popUpTo(Screen.Payment.route) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.Chat.route) {
            val sessionManager: SessionManager = get()
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)
            val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)

            ChatScreen(
                isLoggedIn = isLoggedIn,
                themeSetting = themeSetting,
                onBackClick = { navController.popBackStack() },
                onNavigateToLogin = { navController.navigate(Graph.Auth) }
            )
        }

        // --- PROFILE SUB-SCREENS ---
        composable(Screen.EditProfile.route) {
            val sessionManager: SessionManager = get()
            RequireAuth(
                sessionManager,
                navController
            ) { EditProfilePage(onBack = { navController.popBackStack() }) }
        }
        composable(Screen.Settings.route) {
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)
            SettingsPage(
                onBack = { navController.popBackStack() },
                themeSetting = themeSetting
            )
        }

        // --- ADDRESS CRUD (Sudah Terproteksi RequireAuth) ---
        composable(Screen.AddressList.route) {
            val sessionManager: SessionManager = get()
            RequireAuth(sessionManager, navController) {
                AddressListScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToAdd = { navController.navigate(Screen.AddressCreate.route) },
                    onNavigateToEdit = { id ->
                        navController.navigate(Screen.AddressEdit.createRoute(id))
                    }
                )
            }
        }

        composable(Screen.Address.route) {
            val sessionManager: SessionManager = get()
            RequireAuth(sessionManager, navController) {
                AddressListScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToAdd = { navController.navigate(Screen.AddressCreate.route) },
                    onNavigateToEdit = { id ->
                        navController.navigate(Screen.AddressEdit.createRoute(id))
                    }
                )
            }
        }

        composable(Screen.AddressCreate.route) {
            val sessionManager: SessionManager = get()
            RequireAuth(sessionManager, navController) {
                AddressFormScreen(
                    addressId = null,
                    onBack = { navController.popBackStack() },
                    navController = navController,
                    onNavigateToMapPicker = { lat, long ->
                        navController.navigate(Screen.MapPicker.createRoute(lat, long))
                    }
                )
            }
        }

        composable(
            route = Screen.AddressEdit.route,
            arguments = listOf(navArgument("addressId") { type = NavType.StringType })
        ) { backStackEntry ->
            val addressId = backStackEntry.arguments?.getString("addressId")
            val sessionManager: SessionManager = get()

            RequireAuth(sessionManager, navController) {
                AddressFormScreen(
                    addressId = addressId,
                    onBack = { navController.popBackStack() },
                    navController = navController,
                    onNavigateToMapPicker = { lat, long ->
                        navController.navigate(Screen.MapPicker.createRoute(lat, long))
                    }
                )
            }
        }

        // --- SUCCESS SCREEN ---
        composable(
            route = Screen.OrderSuccess.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType; nullable = true },
                navArgument("paymentGroupId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            val groupId = backStackEntry.arguments?.getString("paymentGroupId")

            OrderSuccessScreen(
                orderId = orderId,
                paymentGroupId = groupId,
                onNavigateToHistory = {
                    if (groupId != null) {
                        navController.navigate(Screen.TransactionGroupDetail.createRoute(groupId)) {
                            popUpTo(Screen.Dashboard.route)
                        }
                    } else {
                        navController.navigate(Screen.Transaction.route) {
                            popUpTo(Screen.Dashboard.route)
                        }
                    }
                }
            )
        }

        // --- AUTH GRAPH ---
        authGraph(
            navController,
            onNavigateToTerms = { navController.navigate(Screen.TermsAndConditions.route) })
    }
}

// ... (authGraph tetap sama)
fun NavGraphBuilder.authGraph(navController: NavHostController, onNavigateToTerms: () -> Unit) {
    navigation(startDestination = Screen.Welcome.route, route = Graph.Auth) {
        composable(Screen.Welcome.route) {
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)
            val sessionManager: SessionManager = get()
            RedirectIfLoggedIn(sessionManager, navController) {
                GetStarted(
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    themeSetting = themeSetting,
                )
            }
        }
        composable(Screen.Login.route) {
            val viewModel: SignInViewModel = koinViewModel()
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)
            LoginScreen(
                viewModel = viewModel,
                onSignInSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                themeSetting = themeSetting
            )
        }
        composable(Screen.Register.route) {
            val sessionManager: SessionManager = get()
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)
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
                    themeSetting = themeSetting
                )
            }
        }
        composable(Screen.ForgotPassword.route) {
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)
            ForgotPasswordStepEmailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToOtp = { email ->
                    navController.navigate(Screen.ForgotPasswordOtp.createRoute(email))
                },
                themeSetting = themeSetting
            )
        }
        composable(
            route = Screen.ForgotPasswordOtp.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val sessionManager: SessionManager = get()
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)
            ForgotPasswordStepOtpScreen(
                email = email,
                onNavigateBack = { navController.popBackStack() },
                onResetSuccess = {
                    kotlinx.coroutines.runBlocking { sessionManager.clearAuthData() }
                    navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                },
                themeSetting = themeSetting
            )
        }
    }
}