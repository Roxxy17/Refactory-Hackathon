package com.example.kalanacommerce // Sesuaikan dengan package name baru jika berbeda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// Import semua screen Anda
import com.example.kalanacommerce.ui.screen.auth.FirstScreen
import com.example.kalanacommerce.ui.screen.auth.ForgotPasswordScreen
import com.example.kalanacommerce.ui.screen.auth.LoginScreen
import com.example.kalanacommerce.ui.screen.auth.RegisterScreen
// Import Theme Anda
import com.example.kalanacommerce.ui.theme.KalanaCommerceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KalanaCommerceTheme {
                AppNavigation() // Panggil navi gasi Anda
            }
        }
    }
}

// --- TEMPEL AppNavigation() DARI PROYEK LAMA DI SINI ---
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            FirstScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("login") {
            LoginScreen(
                onNavigateToRegister = { navController.navigate("register") { popUpTo("login") { inclusive = true } } },
                onNavigateToForgotPassword = { navController.navigate("forgot_password") }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate("login") { popUpTo("register") { inclusive = true } } }
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}