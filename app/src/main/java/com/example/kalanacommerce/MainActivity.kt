package com.example.kalanacommerce

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.kalanacommerce.navigation.AppNavGraph
import com.example.kalanacommerce.navigation.Screen
import com.example.kalanacommerce.front.theme.KalanaCommerceTheme
import com.example.kalanacommerce.back.data.local.datastore.SessionManager
import org.koin.androidx.compose.get

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // HAPUS baris installSplashScreen() jika ada
        // HAPUS baris setKeepOnScreenCondition

        setContent {
            KalanaCommerceTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    // SessionManager dipanggil di sini karena SplashActivity sudah selesai tugasnya
    val sessionManager: SessionManager = get()

    // Ambil status login
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = false)

    // Tentukan mau ke Dashboard atau Welcome
    val startDestination = if (isLoggedIn) Screen.Dashboard.route else Screen.Welcome.route

    AppNavGraph(startDestination = startDestination)
}