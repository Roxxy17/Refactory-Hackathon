package com.example.kalanacommerce

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.kalanacommerce.navigation.AppNavGraph // Pastikan import ini benar
import com.example.kalanacommerce.front.theme.KalanaCommerceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KalanaCommerceTheme {
                // Hapus semua logika 'isLoggedIn' dan 'startDestination' dari sini.
                // Cukup panggil AppNavGraph secara langsung.
                AppNavGraph()
            }
        }
    }
}
