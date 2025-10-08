package com.example.kalanacommerce

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.kalanacommerce.navigation.AppNavGraph
import com.example.kalanacommerce.navigation.Screen
import com.example.kalanacommerce.ui.theme.KalanaCommerceTheme
import com.example.kalanacommerce.data.local.SessionManager
import org.koin.androidx.compose.get

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KalanaCommerceTheme {
                // Panggil MainApp di sini
                MainApp()
            }
        }
    }
}

/**
 * Composable utama yang menangani penentuan layar awal (startDestination)
 * berdasarkan status login dari SessionManager.
 */
@Composable
fun MainApp() {
    // Dapatkan SessionManager dari Koin
    val sessionManager: SessionManager = get()

    // Ambil status login dari DataStore.
    // initial = null menunjukkan bahwa DataStore masih dalam proses pembacaan (loading state).
    val isLoggedIn by sessionManager.isLoggedInFlow.collectAsState(initial = null)

    // Tampilkan UI yang sesuai setelah status login diketahui
    when (isLoggedIn) {
        null -> {
            // State awal: DataStore sedang dibaca. Tampilkan layar loading.
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        true -> {
            // Jika sudah login, langsung ke Dashboard
            // Catatan: Pastikan Screen.Dashboard.route sudah didefinisikan dengan benar.
            AppNavGraph(startDestination = Screen.Dashboard.route)
        }
        false -> {
            // Jika belum login, mulai dari alur Welcome/Login
            // Catatan: Pastikan Screen.Welcome.route sudah didefinisikan dengan benar.
            AppNavGraph(startDestination = Screen.Welcome.route)
        }
    }
}