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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
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
    var isFabMenuExpanded by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val primaryColor = Color(0xFF069C6F)
    val secondaryColor = Color(0xFF027B58)

    val fabSize = 64.dp
    val bottomBarHeight = 60.dp
    val fabPadding = 12.dp
    val cornerRadius = 16.dp

    val fabAnimationSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    val rotation by animateFloatAsState(targetValue = if (isFabMenuExpanded) 45f else 0f, label = "rotation")
    val fabMenuScale by animateFloatAsState(targetValue = if (isFabMenuExpanded) 1f else 0f, fabAnimationSpec, label = "scale")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(bottomBarHeight + fabSize / 2 + 16.dp) // Beri ruang ekstra untuk shadow
    ) {
        // --- BottomAppBar dengan Lekukan dan BAYANGAN KUSTOM ---
        BottomAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .drawBehind {
                    val fabSizePx = fabSize.toPx()
                    val fabPaddingPx = fabPadding.toPx()
                    val cutoutRadius = (fabSizePx / 2f) + fabPaddingPx
                    val cutoutHeight = cutoutRadius
                    val handleWidth = cutoutRadius / 1.5f

                    val shadowPath = Path().apply {
                        moveTo((size.width / 2) - cutoutRadius - handleWidth, 0f)
                        cubicTo(
                            x1 = (size.width / 2) - cutoutRadius, y1 = 0f,
                            x2 = (size.width / 2) - cutoutRadius, y2 = cutoutHeight,
                            x3 = size.width / 2, y3 = cutoutHeight
                        )
                        cubicTo(
                            x1 = (size.width / 2) + cutoutRadius, y1 = cutoutHeight,
                            x2 = (size.width / 2) + cutoutRadius, y2 = 0f,
                            x3 = (size.width / 2) + cutoutRadius + handleWidth, y3 = 0f
                        )
                    }

                    drawIntoCanvas { canvas ->
                        val paint = Paint()
                        val frameworkPaint = paint.asFrameworkPaint()
                        frameworkPaint.color = Color.Transparent.toArgb()
                        frameworkPaint.setShadowLayer(
                            18.dp.toPx(),      // Radius blur bayangan
                            0f,               // Offset X
                            -6f,              // Offset Y (negatif agar bayangan ke atas)
                            Color.Green.copy(alpha = 0.3f).toArgb() // Warna bayangan
                        )
                        canvas.drawPath(shadowPath, paint)
                    }
                }
                .clip(convexBottomBarShape(fabSize, fabPadding, cornerRadius)),
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
                        selectedIconColor = primaryColor, selectedTextColor = primaryColor,
                        unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == items[1].route,
                    onClick = { onItemClick(items[1]) },
                    icon = { Icon(imageVector = items[1].icon, contentDescription = items[1].title) },
                    label = if (currentRoute == items[1].route) { { Text(text = items[1].title) } } else null,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryColor, selectedTextColor = primaryColor,
                        unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.width(fabSize))
                NavigationBarItem(
                    selected = currentRoute == items[2].route,
                    onClick = { onItemClick(items[2]) },
                    icon = { Icon(imageVector = items[2].icon, contentDescription = items[2].title) },
                    label = if (currentRoute == items[2].route) { { Text(text = items[2].title) } } else null,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryColor, selectedTextColor = primaryColor,
                        unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == items[3].route,
                    onClick = { onItemClick(items[3]) },
                    icon = { Icon(imageVector = items[3].icon, contentDescription = items[3].title) },
                    label = if (currentRoute == items[3].route) { { Text(text = items[3].title) } } else null,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryColor, selectedTextColor = primaryColor,
                        unselectedIconColor = Color.Gray, indicatorColor = Color.Transparent
                    )
                )
            }
        }

        // --- FAB dan Sub-Menu ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .offset(y = -(fabSize / 2) - 24.dp)
        ) {
            FloatingActionButton(
                onClick = { /* TODO: Aksi Chat */ },
                modifier = Modifier.size(56.dp).align(Alignment.Center).scale(fabMenuScale)
                    .offset(x = (-80).dp, y = (-20).dp),
                shape = CircleShape, containerColor = Color.White
            ) {
                Icon(Icons.Default.Chat, contentDescription = "Pesan", tint = primaryColor)
            }
            FloatingActionButton(
                onClick = { /* TODO: Aksi Keranjang */ },
                modifier = Modifier.size(56.dp).align(Alignment.Center).scale(fabMenuScale)
                    .offset(x = 80.dp, y = (-20).dp),
                shape = CircleShape, containerColor = Color.White
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Keranjang", tint = primaryColor)
            }

            // Tumpukan komponen untuk FAB dan bayangannya
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(fabSize)
                    .align(Alignment.Center)
            ) {
                // LAPISAN 1: Bayangan Gradasi Hitam ke Transparan
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        // --- PERBAIKAN DI SINI ---
                        .drawBehind {
                            // Di dalam drawBehind, kita punya akses ke 'size'
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color.Green.copy(alpha = 0.3f), Color.Transparent),
                                    radius = size.width / 2.0f // Ini sekarang valid
                                )
                            )
                        }
                )

                // LAPISAN 2: FAB Utama dengan Gradasi Hijau
                FloatingActionButton(
                    onClick = { isFabMenuExpanded = !isFabMenuExpanded },
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    containerColor = Color.Transparent, // Biarkan transparan agar gradasi hijau terlihat
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                ) {
                    // Box ini sekarang bertanggung jawab untuk gradasi hijau
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(primaryColor, secondaryColor)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFabMenuExpanded) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = if (isFabMenuExpanded) "Tutup Menu" else "Buka Menu",
                            modifier = Modifier
                                .size(32.dp)
                                .rotate(rotation)
                        )
                    }
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
