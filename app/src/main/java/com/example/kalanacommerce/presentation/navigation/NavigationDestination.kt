// File: D:/My-Project/KalanaCommerce/app/src/main/java/com/example/kalanacommerce/navigation/Screen.kt

package com.example.kalanacommerce.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

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

}

// ! Navigation Bottom Bar Screen

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Eksplor : BottomBarScreen("eksplor", "Eksplor", Icons.Default.Home)
    object Pencarian : BottomBarScreen("pencarian", "Pencarian", Icons.Default.Search)
    object Riwayat : BottomBarScreen("riwayat", "Riwayat", Icons.Default.History)
    object Profile : BottomBarScreen("profile", "Profile", Icons.Default.Person)
}
