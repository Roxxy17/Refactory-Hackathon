package com.example.kalanacommerce.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    // ... item lain yang sudah ada ...
    object Eksplor : BottomBarScreen("eksplor", "Eksplor", Icons.Default.Home)
    object Pencarian : BottomBarScreen("pencarian", "Pencarian", Icons.Default.Search)
    object Riwayat : BottomBarScreen("riwayat", "Riwayat", Icons.Default.History)
    object Profile : BottomBarScreen("profile", "Profile", Icons.Default.Person)

    // --- TAMBAHKAN DEFINISI INI ---
    object Action : BottomBarScreen("action", "Action", Icons.Default.Chat) // Atau Icons.Default.Add
}
