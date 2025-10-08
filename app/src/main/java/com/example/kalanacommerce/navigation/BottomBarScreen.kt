package com.example.kalanacommerce.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Eksplor : BottomBarScreen(
        route = "eksplor",
        title = "Eksplor",
        icon = Icons.Outlined.Explore
    )
    object Pencarian : BottomBarScreen(
        route = "pencarian",
        title = "Pencarian",
        icon = Icons.Outlined.Search
    )
    object Riwayat : BottomBarScreen(
        route = "riwayat",
        title = "Riwayat",
        icon = Icons.Outlined.ReceiptLong
    )
    object Profile : BottomBarScreen(
        route = "profile",
        title = "Profile",
        icon = Icons.Outlined.AccountCircle
    )
}