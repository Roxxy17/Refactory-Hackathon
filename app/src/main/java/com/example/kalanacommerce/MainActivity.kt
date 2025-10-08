package com.example.kalanacommerce

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.kalanacommerce.navigation.AppNavGraph // Import file navigasi baru
import com.example.kalanacommerce.ui.theme.KalanaCommerceTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KalanaCommerceTheme {
                // Cukup panggil grafik navigasi utama di sini
                AppNavGraph()
            }
        }
    }
}