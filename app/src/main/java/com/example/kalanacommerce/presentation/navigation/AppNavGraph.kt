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
import com.example.kalanacommerce.presentation.screen.dashboard.history.group.TransactionGroupScreen // Import screen baru ini
import com.example.kalanacommerce.presentation.screen.dashboard.map.MapPickerScreen
import com.example.kalanacommerce.presentation.screen.dashboard.map.MapRouteScreen
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
            navController.navigate(Graph.Auth) {
                popUpTo(0) {
                    inclusive = true
                }
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
            popUpTo(
                0
            ) { inclusive = true }
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
                    popUpTo(
                        0
                    ) { inclusive = true }
                }
            }
        }

        // Di AppNavGraph.kt

        composable(
            route = Screen.MapRoute.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType }, // Gunakan StringType biar aman passing via URL
                navArgument("long") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Ambil data
            val latString = backStackEntry.arguments?.getString("lat") ?: "0.0"
            val longString = backStackEntry.arguments?.getString("long") ?: "0.0"


            val lat = latString.toDoubleOrNull() ?: -7.7717 // Default Jogja jika error
            val long = longString.toDoubleOrNull() ?: 110.377

            MapRouteScreen(
                userLat = lat,   // [BARU] Kirim ke Screen
                userLong = long, // [BARU] Kirim ke Screen
                onBackClick = { navController.popBackStack() }
            )
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
                    // [LOGIC PENGEMBALIAN DATA]
                    // Kita set data ke "SavedStateHandle" milik screen SEBELUMNYA (Form Address)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("location_result", "$lat,$long") // Kirim string biar gampang

                    navController.popBackStack() // Tutup map picker
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
                onStoreClick = { outletId ->
                    navController.navigate(
                        Screen.DetailStore.createRoute(
                            outletId
                        )
                    )
                },
                onNavigateToCheckout = { itemIds ->
                    navController.navigate(
                        Screen.Checkout.createRoute(
                            itemIds
                        )
                    )
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
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

            DetailStorePage(
                outletId = outletId,
                themeSetting = themeSetting,
                onBackClick = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate(
                        Screen.DetailProduct.createRoute(
                            productId
                        )
                    )
                },
                onNavigateToCart = {
                    navController.navigate(Screen.Cart.route)
                }
            )
        }

        // --- TRANSACTION FLOW ---
        composable(Screen.Cart.route) {
            val sessionManager: SessionManager = get()
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)

            // [BARU] Ambil status login langsung di sini
            val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)

            // [HAPUS] RequireAuth(...) { ... }
            // Karena CartScreen sekarang menangani logika UI "Belum Login" sendiri
            CartScreen(
                isLoggedIn = isLoggedIn, // Pass status login
                themeSetting = themeSetting,
                onBackClick = { navController.popBackStack() },
                onNavigateToLogin = { navController.navigate(Graph.Auth) }, // Pass aksi login
                onNavigateToHome = { // Pass aksi kembali ke Home (jika cart kosong)
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(0)
                    }
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

        composable(
            route = Screen.Checkout.route,
            arguments = listOf(navArgument("itemIds") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemIds = backStackEntry.arguments?.getString("itemIds") ?: ""
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)

            CheckoutScreen(
                itemIdsString = itemIds,
                themeSetting = themeSetting,
                onBackClick = { navController.popBackStack() },

                onNavigateToPayment = { paymentUrl, orderId, paymentGroupId ->
                    val encodedUrl = URLEncoder.encode(paymentUrl, StandardCharsets.UTF_8.toString())

                    // [FIX] Gunakan createRoute yang baru
                    navController.navigate(
                        Screen.Payment.createRoute(encodedUrl, orderId, paymentGroupId)
                    )
                },
                onNavigateToAddress = {
                    navController.navigate(Screen.AddressList.route)
                }
            )
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
                    // [FIX] Tambahkan ini: Navigasi ke OrderSuccess untuk tracking
                    onNavigateToMaps = { orderId ->
                        navController.navigate(Screen.OrderSuccess.createRoute(orderId = orderId))
                    }
                )
            }
        }

        // [BARU] Screen Gabungan (Multi-Toko)
        composable(
            route = Screen.TransactionGroupDetail.route,
            arguments = listOf(navArgument("paymentGroupId") { type = NavType.StringType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("paymentGroupId") ?: ""
            val themeManager: ThemeManager = get()
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)

            TransactionGroupScreen(
                paymentGroupId = groupId,
                themeSetting = themeSetting,
                onBackClick = { navController.popBackStack() },
                onNavigateToOrderDetail = { orderId ->
                    navController.navigate(Screen.DetailOrder.createRoute(orderId))
                },
                // [FIX] Tambahkan ini: Navigasi ke OrderSuccess per item
                onNavigateToMaps = { orderId ->
                    navController.navigate(Screen.OrderSuccess.createRoute(orderId = orderId))
                }
            )
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
                // [PERBAIKAN] Menerima 3 parameter: url, id, groupId
                onNavigateToPayment = { snapUrl, id, groupId ->
                    val encodedUrl = URLEncoder.encode(snapUrl, StandardCharsets.UTF_8.toString())
                    // Gunakan createRoute yang aman
                    navController.navigate(Screen.Payment.createRoute(encodedUrl, id, groupId))
                },
                onNavigateToMaps = { id ->
                    navController.navigate(Screen.OrderSuccess.createRoute(orderId = id))
                }
            )
        }

        composable(
            route = Screen.Payment.route,
            arguments = listOf(
                navArgument("paymentUrl") { type = NavType.StringType },
                navArgument("orderId") { type = NavType.StringType },
                // Tambahkan argumen optional untuk Group ID
                navArgument("paymentGroupId") {
                    type = NavType.StringType; nullable = true; defaultValue = null
                }
            )
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("paymentUrl") ?: ""
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val paymentGroupId = backStackEntry.arguments?.getString("paymentGroupId")

            PaymentScreen(
                paymentUrl = url,
                orderId = orderId,
                onPaymentFinished = { _ ->
                    val targetRoute = Screen.OrderSuccess.createRoute(
                        orderId = if (paymentGroupId == null) orderId else null,
                        paymentGroupId = paymentGroupId
                    )

                    navController.navigate(targetRoute) {
                        // Hapus PaymentScreen dari stack agar kalau di-back tidak balik ke webview
                        popUpTo(Screen.Payment.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
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
            SettingsPage(onBack = { navController.popBackStack() })
        }

        // --- ADDRESS CRUD ---
        composable(Screen.AddressList.route) {
            val sessionManager: SessionManager = get()
            RequireAuth(sessionManager, navController) {
                AddressListScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToAdd = { navController.navigate(Screen.AddressCreate.route) },
                    onNavigateToEdit = { id ->
                        navController.navigate(
                            Screen.AddressEdit.createRoute(
                                id
                            )
                        )
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
                        navController.navigate(
                            Screen.AddressEdit.createRoute(
                                id
                            )
                        )
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

                    // [BARU] Pass NavController
                    navController = navController,

                    // [BARU] Aksi buka Map Picker
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

                    // [PERBAIKAN] Tambahkan 2 parameter ini:
                    navController = navController,
                    onNavigateToMapPicker = { lat, long ->
                        navController.navigate(Screen.MapPicker.createRoute(lat, long))
                    }
                )
            }
        }

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
                    // Saat klik selesai, baru ke history/group detail
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

// --- AUTH GRAPH BUILDER ---
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
                        popUpTo(0) {
                            inclusive = true
                        }
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
                            popUpTo(
                                Screen.Register.route
                            ) { inclusive = true }
                        }
                    },
                    onContinue = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) {
                                inclusive = true
                            }
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
                    navController.navigate(
                        Screen.ForgotPasswordOtp.createRoute(
                            email
                        )
                    )
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