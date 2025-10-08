package com.example.kalanacommerce

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kalanacommerce.ui.screen.LoginScreen // Asumsi Anda punya LoginScreen
import com.example.kalanacommerce.ui.screen.RegisterScreen
import com.example.kalanacommerce.ui.theme.KalanaCommerceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KalanaCommerceTheme {
                // 1. Buat NavController
                val navController = rememberNavController()

                // 2. Siapkan NavHost untuk mendefinisikan rute navigasi
                NavHost(
                    navController = navController,
                    startDestination = "login" // Rute awal saat aplikasi dibuka
                ) {
                    // Rute untuk LoginScreen
                    composable("login") {
                        LoginScreen(
                            onNavigateToRegister = {
                                navController.navigate("register")
                            }
                        )
                    }

                    // --- MODIFIKASI BAGIAN INI ---
                    composable("register") {
                        RegisterScreen(
                            onNavigateToLogin = {
                                navController.navigate("login") {
                                    // Ini akan menghapus semua dari back stack hingga "login"
                                    popUpTo("login") {
                                        // 'inclusive = true' berarti "login" yang lama juga ikut dihapus,
                                        // sehingga kita mendapatkan layar login yang baru dan bersih.
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                    // -----------------------------
                }
            }
        }
    }
}
