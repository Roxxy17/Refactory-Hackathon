package com.example.kalanacommerce.navigation

// Sealed class untuk menyimpan semua rute/tujuan navigasi kita
sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object FillAccount : Screen("fill_account")
    object History : Screen("history")
    // Tambahkan rute untuk layar utama aplikasi Anda nanti di sini
    // object Home : Screen("home")
}