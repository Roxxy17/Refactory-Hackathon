package com.example.kalanacommerce.ui.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kalanacommerce.navigation.BottomBarScreen
import com.example.kalanacommerce.ui.components.AppBottomNavigationBar

@Composable
fun DashboardScreen(
    // PERUBAHAN 1: Tambahkan parameter onLogout
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val primaryColor = Color(0xFF069C6F)

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(navController = navController) { screen ->
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Aksi untuk tombol tengah */ },
                containerColor = primaryColor,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.ChatBubble, contentDescription = "Pesan")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        // PERUBAHAN 2: Teruskan onLogout ke NavGraph internal
        DashboardNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            onLogout = onLogout
        )
    }
}

// Ini adalah NavHost untuk halaman-halaman di dalam dashboard
@Composable
fun DashboardNavGraph(
    navController: NavHostController,
    modifier: Modifier,
    // PERUBAHAN 3: Terima onLogout di sini juga
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Eksplor.route,
        modifier = modifier
    ) {
        composable(BottomBarScreen.Eksplor.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Halaman Eksplor")
            }
        }
        composable(BottomBarScreen.Pencarian.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Halaman Pencarian")
            }
        }
        composable(BottomBarScreen.Riwayat.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Halaman Riwayat")
            }
        }
        composable(BottomBarScreen.Profile.route) {
            // PERUBAHAN 4: Buat halaman Profile dan tambahkan tombol Logout
            ProfileContent(onLogout = onLogout)
        }
    }
}

/**
 * Composable baru untuk isi Halaman Profile.
 * Ini membuat kode lebih bersih.
 */
@Composable
fun ProfileContent(onLogout: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Halaman Profile")
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onLogout, // Panggil fungsi logout saat tombol diklik
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)) // Warna merah untuk aksi berbahaya
            ) {
                Text("Logout")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    // PERUBAHAN 5: Sediakan fungsi kosong untuk onLogout di preview
    DashboardScreen(onLogout = {})
}
