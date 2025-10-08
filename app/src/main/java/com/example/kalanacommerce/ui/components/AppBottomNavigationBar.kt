package com.example.kalanacommerce.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kalanacommerce.navigation.BottomBarScreen

@Composable
fun AppBottomNavigationBar(
    navController: NavController,
    onItemClick: (BottomBarScreen) -> Unit
) {
    val items = listOf(
        BottomBarScreen.Eksplor,
        BottomBarScreen.Pencarian,
        BottomBarScreen.Riwayat,
        BottomBarScreen.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val primaryColor = Color(0xFF069C6F)

    BottomAppBar(
        containerColor = Color.White,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Item 1 & 2 (Kiri)
            NavigationBarItem(
                selected = currentRoute == items[0].route,
                onClick = { onItemClick(items[0]) },
                icon = { Icon(imageVector = items[0].icon, contentDescription = items[0].title) },
                label = { Text(text = items[0].title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryColor,
                    selectedTextColor = primaryColor,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
            NavigationBarItem(
                selected = currentRoute == items[1].route,
                onClick = { onItemClick(items[1]) },
                icon = { Icon(imageVector = items[1].icon, contentDescription = items[1].title) },
                label = { Text(text = items[1].title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryColor,
                    selectedTextColor = primaryColor,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )

            // Spasi Kosong untuk Floating Action Button
            Spacer(modifier = Modifier.weight(1f))

            // Item 3 & 4 (Kanan)
            NavigationBarItem(
                selected = currentRoute == items[2].route,
                onClick = { onItemClick(items[2]) },
                icon = { Icon(imageVector = items[2].icon, contentDescription = items[2].title) },
                label = { Text(text = items[2].title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryColor,
                    selectedTextColor = primaryColor,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
            NavigationBarItem(
                selected = currentRoute == items[3].route,
                onClick = { onItemClick(items[3]) },
                icon = { Icon(imageVector = items[3].icon, contentDescription = items[3].title) },
                label = { Text(text = items[3].title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryColor,
                    selectedTextColor = primaryColor,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}