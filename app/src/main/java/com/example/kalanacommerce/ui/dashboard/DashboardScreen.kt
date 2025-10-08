package com.example.kalanacommerce.ui.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kalanacommerce.navigation.BottomBarScreen
import com.example.kalanacommerce.ui.components.AppBottomNavigationBar

@Composable
fun DashboardScreen() {
    val navController = rememberNavController()
    val primaryColor = Color(0xFF069C6F)

    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(navController = navController) { screen ->
                navController.navigate(screen.route) {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    // on the back stack as users select items
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
        // Navigasi untuk konten di dalam Dashboard
        DashboardNavGraph(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}

// Ini adalah NavHost untuk halaman-halaman di dalam dashboard
@Composable
fun DashboardNavGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Eksplor.route,
        modifier = modifier
    ) {
        composable(BottomBarScreen.Eksplor.route) {
            // Ganti dengan Composable layar Eksplor Anda
            Box { Text("Halaman Eksplor") }
        }
        composable(BottomBarScreen.Pencarian.route) {
            Box { Text("Halaman Pencarian") }
        }
        composable(BottomBarScreen.Riwayat.route) {
            Box { Text("Halaman Riwayat") }
        }
        composable(BottomBarScreen.Profile.route) {
            Box { Text("Halaman Profile") }
        }
    }
}


@Preview
@Composable
fun DashboardScreenPreview() {
    DashboardScreen()
}