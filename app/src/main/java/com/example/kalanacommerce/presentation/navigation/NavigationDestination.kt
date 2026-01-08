// File: D:/My-Project/KalanaCommerce/app/src/main/java/com/example/kalanacommerce/navigation/Screen.kt

package com.example.kalanacommerce.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.annotation.StringRes
import com.example.kalanacommerce.R

// Objek untuk merepresentasikan nama grafik navigasi bersarang (nested graph)
object Graph {
    const val Auth = "auth_graph"
}

// ! Navigation diluar bottom bar screen

// Sealed class untuk menyimpan semua rute layar (screen routes)
sealed class Screen(val route: String) {
    // Rute untuk layar-layar di dalam Auth Graph
    data object Welcome : Screen("welcome_screen")
    data object Login : Screen("login_screen")
    data object Register : Screen("register_screen")
    data object ForgotPassword : Screen("forgot_password_screen")

    // Rute untuk layar utama
    data object Dashboard : Screen("dashboard_screen")

    // Rute untuk layar profil dan turunannya
    data object Settings : Screen("settings_screen")
    data object TermsAndConditions : Screen("terms_conditions")
    data object EditProfile : Screen("edit_profile_screen")
    data object Address : Screen("address_screen")
    data object HelpCenter : Screen("help_center_screen")

}

// ! Navigation Bottom Bar Screen

sealed class BottomBarScreen(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector
) {
    object Eksplor : BottomBarScreen(
        route = "eksplor_screen",
        title = R.string.nav_explore, // Panggil R.string disini
        icon = Icons.Default.Home // Sesuaikan icon kamu
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
