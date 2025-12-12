package com.example.kalanacommerce.navigation

// Sealed class untuk menyimpan semua rute/tujuan navigasi kita
sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    // Tambahkan rute untuk layar utama aplikasi Anda nanti di sini
    // object Home : Screen("home")
    object Dashboard : Screen("dashboard")
    object TransactionScreen : Screen("transaction_screen")
    object Explore : Screen("explore")
    object History: Screen("history")
    object Profile: Screen("profile")
    object Search: Screen("search")
    object Chat : Screen("chat")

    object EditProfile : Screen("edit_profile_screen")
    object Address : Screen("address_screen")
    object Settings : Screen("settings_screen")

}