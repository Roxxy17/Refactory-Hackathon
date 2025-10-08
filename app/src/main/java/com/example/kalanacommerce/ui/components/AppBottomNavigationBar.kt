package com.example.kalanacommerce.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kalanacommerce.navigation.BottomBarScreen

@Composable
fun AppBottomNavigationBar(
    navController: NavController,
    onItemClick: (BottomBarScreen) -> Unit
) {
    // --- State untuk FAB Menu ---
    var isFabMenuExpanded by remember { mutableStateOf(false) }

    // --- State & Variabel untuk Navigasi ---
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val primaryColor = Color(0xFF069C6F)
    val secondaryColor = Color(0xFF027B58)

    // --- Ukuran & Padding ---
    val fabSize = 64.dp
    val bottomBarHeight = 60.dp

    // --- Konfigurasi Animasi ---
    val fabAnimationSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    val rotation by animateFloatAsState(targetValue = if (isFabMenuExpanded) 45f else 0f, label = "rotation")
    val fabMenuScale by animateFloatAsState(targetValue = if (isFabMenuExpanded) 1f else 0f, fabAnimationSpec, label = "scale")

    // --- Elemen UI ---
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(bottomBarHeight + fabSize / 2) // Beri ruang ekstra untuk FAB yang menonjol
    ) {
        // --- BottomAppBar dengan Cekungan ---
        BottomAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .shadow(
                    elevation = 12.dp,
                    shape = cutoutBottomBarShape(fabSize, 12.dp, 16.dp),
                    clip = false // Penting agar shadow terlihat
                )
                .clip(cutoutBottomBarShape(fabSize, 12.dp, 16.dp)),
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            val items = listOf(
                BottomBarScreen.Eksplor,
                BottomBarScreen.Pencarian,
                BottomBarScreen.Riwayat,
                BottomBarScreen.Profile
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Item 1 & 2 (Kiri)
                NavigationBarItem(
                    selected = currentRoute == items[0].route,
                    onClick = { onItemClick(items[0]) },
                    icon = { Icon(imageVector = items[0].icon, contentDescription = items[0].title) },
                    label = if (currentRoute == items[0].route) { { Text(text = items[0].title) } } else null,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryColor,
                        selectedTextColor = primaryColor,
                        unselectedIconColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == items[1].route,
                    onClick = { onItemClick(items[1]) },
                    icon = { Icon(imageVector = items[1].icon, contentDescription = items[1].title) },
                    label = if (currentRoute == items[1].route) { { Text(text = items[1].title) } } else null,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryColor,
                        selectedTextColor = primaryColor,
                        unselectedIconColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )

                // Spacer untuk memberikan ruang bagi FAB
                Spacer(modifier = Modifier.width(fabSize))

                // Item 3 & 4 (Kanan)
                NavigationBarItem(
                    selected = currentRoute == items[2].route,
                    onClick = { onItemClick(items[2]) },
                    icon = { Icon(imageVector = items[2].icon, contentDescription = items[2].title) },
                    label = if (currentRoute == items[2].route) { { Text(text = items[2].title) } } else null,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryColor,
                        selectedTextColor = primaryColor,
                        unselectedIconColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == items[3].route,
                    onClick = { onItemClick(items[3]) },
                    icon = { Icon(imageVector = items[3].icon, contentDescription = items[3].title) },
                    label = if (currentRoute == items[3].route) { { Text(text = items[3].title) } } else null,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryColor,
                        selectedTextColor = primaryColor,
                        unselectedIconColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }

        // --- FAB dan Sub-Menu ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                // --- PERUBAHAN DI SINI ---
                // Naikkan posisi tombol lebih tinggi lagi
                .offset(y = -(fabSize / 2) - 24.dp)
        ) {
            // Tombol Sub-Menu Kiri
            FloatingActionButton(
                onClick = { /* TODO: Aksi Chat */ },
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.Center)
                    .scale(fabMenuScale)
                    .offset(x = (-80).dp, y = (-20).dp), // Geser ke kiri dan sedikit ke atas
                shape = CircleShape,
                containerColor = Color.White
            ) {
                Icon(Icons.Default.Chat, contentDescription = "Pesan", tint = primaryColor)
            }

            // Tombol Sub-Menu Kanan
            FloatingActionButton(
                onClick = { /* TODO: Aksi Keranjang */ },
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.Center)
                    .scale(fabMenuScale)
                    .offset(x = 80.dp, y = (-20).dp), // Geser ke kanan dan sedikit ke atas
                shape = CircleShape,
                containerColor = Color.White
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Keranjang", tint = primaryColor)
            }

            // FAB Utama dengan Gradasi
            Box(
                modifier = Modifier
                    .size(fabSize)
                    .align(Alignment.Center)
                    .shadow(elevation = 8.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(primaryColor, secondaryColor)
                        )
                    )
            ) {
                FloatingActionButton(
                    onClick = { isFabMenuExpanded = !isFabMenuExpanded },
                    modifier = Modifier.fillMaxSize(),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp), // Matikan shadow internal FAB
                    containerColor = Color.Transparent, // Buat transparan agar background gradasi terlihat
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (isFabMenuExpanded) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = if (isFabMenuExpanded) "Tutup Menu" else "Buka Menu",
                        modifier = Modifier.size(32.dp).rotate(rotation)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun AppBottomNavigationBarPreview() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            AppBottomNavigationBar(navController = navController) { /* do nothing */ }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues))
    }
}
