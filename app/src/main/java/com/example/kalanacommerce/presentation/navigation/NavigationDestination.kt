// File: presentation/navigation/Screen.kt

package com.example.kalanacommerce.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.kalanacommerce.R

object Graph {
    const val Auth = "auth_graph"
}

// Sealed class untuk menyimpan semua rute layar (screen routes)
sealed class Screen(val route: String) {
    // --- Auth ---
    data object Welcome : Screen("welcome_screen")
    data object Login : Screen("login_screen")
    data object Register : Screen("register_screen")
    data object ForgotPassword : Screen("forgot_password_screen")

    // Route dengan argumen (Helper function untuk memudahkan navigasi)
    data object ForgotPasswordOtp : Screen("forgot_password_otp/{email}") {
        fun createRoute(email: String) = "forgot_password_otp/$email"
    }

    // --- Main ---
    data object Dashboard : Screen("dashboard_screen")

    // --- Features ---
    data object Cart : Screen("cart_screen")
    data object Chat : Screen("chat_screen")

    // --- Detail Screens ---
    data object DetailProduct : Screen("detail_product/{productId}") {
        fun createRoute(productId: String) = "detail_product/$productId"
    }

    data object DetailStore : Screen("detail_store/{outletId}") {
        fun createRoute(outletId: String) = "detail_store/$outletId"
    }

    // --- Transaction & History ---
    data object Transaction : Screen("transaction_screen") // History Tab Full

    data object DetailOrder : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: String) = "order_detail/$orderId"
    }

    // --- Checkout & Payment ---
    data object Checkout : Screen("checkout_screen/{itemIds}") {
        fun createRoute(itemIds: String) = "checkout_screen/$itemIds"
    }

    data object Payment : Screen("payment_screen/{paymentUrl}/{orderId}?paymentGroupId={paymentGroupId}") {
        fun createRoute(paymentUrl: String, orderId: String, paymentGroupId: String? = null): String {
            return if (paymentGroupId != null) {
                "payment_screen/$paymentUrl/$orderId?paymentGroupId=$paymentGroupId"
            } else {
                "payment_screen/$paymentUrl/$orderId"
            }
        }
    }
    data object TransactionGroupDetail : Screen("transaction_group_detail/{paymentGroupId}") {
        fun createRoute(paymentGroupId: String) = "transaction_group_detail/$paymentGroupId"
    }

    // --- Profile & Settings ---
    data object Settings : Screen("settings_screen")
    data object TermsAndConditions : Screen("terms_conditions")
    data object EditProfile : Screen("edit_profile_screen")
    data object HelpCenter : Screen("help_center_screen")

    // --- Address ---
    data object Address : Screen("address_screen") // List
    data object AddressCreate : Screen("address_create") // Form Create
    data object AddressList : Screen("address_list")

    data object AddressEdit : Screen("address_edit/{addressId}") {
        fun createRoute(addressId: String) = "address_edit/$addressId"
    }

    data object MapRoute : Screen("map_route_screen/{lat}/{long}") {
        fun createRoute(lat: Double, long: Double) = "map_route_screen/$lat/$long"
    }

    data object MapPicker : Screen("map_picker_screen?lat={lat}&long={long}") {
        fun createRoute(lat: Double = 0.0, long: Double = 0.0) = "map_picker_screen?lat=$lat&long=$long"
    }

    data object OrderSuccess : Screen("order_success?orderId={orderId}&paymentGroupId={paymentGroupId}") {
        fun createRoute(orderId: String? = null, paymentGroupId: String? = null): String {
            // Helper simple logic
            return if (paymentGroupId != null) {
                "order_success?paymentGroupId=$paymentGroupId"
            } else {
                "order_success?orderId=$orderId"
            }
        }
    }

}

// Navigation Bottom Bar Screen
sealed class BottomBarScreen(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector
) {
    object Eksplor : BottomBarScreen(
        route = "eksplor_screen",
        title = R.string.nav_explore,
        icon = Icons.Default.Home
    )

    object Pencarian : BottomBarScreen(
        route = "pencarian_screen",
        title = R.string.nav_search,
        icon = Icons.Default.Search
    )

    object Riwayat : BottomBarScreen(
        route = "riwayat_screen",
        title = R.string.nav_history,
        icon = Icons.Default.History
    )

    object Profile : BottomBarScreen(
        route = "profile_screen",
        title = R.string.nav_profile,
        icon = Icons.Default.Person
    )
}