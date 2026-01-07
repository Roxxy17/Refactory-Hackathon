package com.example.kalanacommerce.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.kalanacommerce.R
import com.example.kalanacommerce.presentation.navigation.BottomBarScreen
import kotlin.math.roundToInt

@Composable
fun AppBottomNavigationBar(
    navController: NavController,
    mainNavController: NavController,
    onItemClick: (BottomBarScreen) -> Unit
) {
    // --- 1. CONFIG & STATE ---
    var isMenuExpanded by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current

    // --- 2. THEME COLORS ---
    val isDark = isSystemInDarkTheme()
    val barBgColor = MaterialTheme.colorScheme.surface
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    // Warna untuk menu popup (Kontras dengan background)
    val popupBgColor = if (isDark) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surface
    val popupContentColor = MaterialTheme.colorScheme.onSurface

    // Shadow Colors (Glow Effect)
    // Di Dark mode, shadow hitam pekat tidak terlihat, jadi kita kurangi atau ganti strategi (disini pakai elevation tonal)
    val mainShadowColor = if (isDark) Color.Black.copy(alpha = 0.5f) else primaryColor.copy(alpha = 0.25f)
    val buttonShadowColor = if (isDark) Color.Black.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.15f)

    // Gradient Tombol Tengah
    val centerButtonGradient = Brush.verticalGradient(
        colors = listOf(primaryColor, secondaryColor)
    )

    // --- 3. ANIMATION STATE (Diagonal Movement) ---
    // Kita gunakan Transition untuk mengontrol semua animasi menu sekaligus
    val transition = updateTransition(targetState = isMenuExpanded, label = "MenuTransition")

    // Offset Jarak Diagonal (X dan Y)
    // Semakin besar angka minus Y, semakin tinggi naiknya.
    // Semakin besar angka X, semakin lebar menyampingnya.
    val moveY by transition.animateDp(
        transitionSpec = { spring(dampingRatio = 0.6f, stiffness = 600f) },
        label = "OffsetY"
    ) { if (it) (-90).dp else 0.dp } // Naik 90dp saat expanded

    val moveX by transition.animateDp(
        transitionSpec = { spring(dampingRatio = 0.6f, stiffness = 600f) },
        label = "OffsetX"
    ) { if (it) 75.dp else 0.dp } // Menyamping 75dp saat expanded

    val menuAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 200) },
        label = "Alpha"
    ) { if (it) 1f else 0f }

    val menuScale by transition.animateFloat(
        transitionSpec = { spring(dampingRatio = 0.6f) },
        label = "Scale"
    ) { if (it) 1f else 0.5f }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp), // Area diperbesar agar animasi diagonal muat
        contentAlignment = Alignment.BottomCenter
    ) {

        // --- 4. POP-UP MENUS (DIAGONAL) ---

        // TOMBOL KIRI (PESAN) - Diagonal Kiri Atas (-X, -Y)
        Box(
            modifier = Modifier
                .offset { IntOffset(x = -moveX.toPx().roundToInt(), y = moveY.toPx().roundToInt()) }
                .align(Alignment.BottomCenter) // Start dari tengah bawah
                .padding(bottom = 24.dp) // Posisi awal di belakang tombol FAB
                .alpha(menuAlpha)
                .scale(menuScale)
        ) {
            DiagonalMenuButton(
                icon = Icons.Default.Chat,
                label = stringResource(R.string.pesan),
                bgColor = popupBgColor,
                textColor = popupContentColor,
                iconColor = primaryColor,
                shadowColor = buttonShadowColor,
                onClick = {
                    isMenuExpanded = false
                    mainNavController.navigate("chat_screen")
                }
            )
        }

        // TOMBOL KANAN (KERANJANG) - Diagonal Kanan Atas (+X, -Y)
        Box(
            modifier = Modifier
                .offset { IntOffset(x = moveX.toPx().roundToInt(), y = moveY.toPx().roundToInt()) }
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .alpha(menuAlpha)
                .scale(menuScale)
        ) {
            DiagonalMenuButton(
                icon = Icons.Default.ShoppingCart,
                label = stringResource(R.string.keranjang),
                bgColor = popupBgColor,
                textColor = popupContentColor,
                iconColor = primaryColor,
                shadowColor = buttonShadowColor,
                onClick = {
                    isMenuExpanded = false
                    mainNavController.navigate("transaction_screen")
                }
            )
        }

        // --- 5. MAIN BAR (Navigation) ---
        Surface(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .height(70.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp, // Atur elevasi bayangan
                    shape = RoundedCornerShape(24.dp),
                    spotColor = Color.Black.copy(alpha = 0.3f), // Warna spot shadow lebih gelap
                    ambientColor = Color.Black.copy(alpha = 0.1f) // Warna ambient shadow lebih pudar
                )
                .align(Alignment.BottomCenter),
            color = barBgColor,
            tonalElevation = 3.dp, // Penting untuk Dark Mode
            shape = RoundedCornerShape(24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // KIRI
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                    NavBarItem(BottomBarScreen.Eksplor, currentRoute, primaryColor) { onItemClick(it) }
                    NavBarItem(BottomBarScreen.Pencarian, currentRoute, primaryColor) { onItemClick(it) }
                }

                // SPACER TENGAH
                Spacer(modifier = Modifier.width(68.dp))

                // KANAN
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                    NavBarItem(BottomBarScreen.Riwayat, currentRoute, primaryColor) { onItemClick(it) }
                    NavBarItem(BottomBarScreen.Profile, currentRoute, primaryColor) { onItemClick(it) }
                }
            }
        }

        // --- 6. CENTER FAB (Floating Action Button) ---
        val rotation by animateFloatAsState(
            targetValue = if (isMenuExpanded) 135f else 0f,
            animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
            label = "FabRot"
        )
        val fabScale by animateFloatAsState(targetValue = if (isMenuExpanded) 0.9f else 1f, label = "FabScale")

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp) // Naikkan posisi agar duduk di atas bar
                .size(64.dp)
                .scale(fabScale)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    spotColor = primaryColor.copy(alpha = 0.6f) // Glowing shadow hijau
                )
                .clip(CircleShape)
                .background(centerButtonGradient)
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    isMenuExpanded = !isMenuExpanded
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Menu",
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .rotate(rotation)
            )
        }
    }
}

// --- SUB COMPONENT BARU: DIAGONAL BUTTON ---
@Composable
fun DiagonalMenuButton(
    icon: ImageVector,
    label: String,
    bgColor: Color,
    textColor: Color,
    iconColor: Color,
    shadowColor: Color,
    onClick: () -> Unit
) {
    // Kita buat layout vertikal (Icon besar di atas, Label kecil di bawah)
    // agar terlihat seperti "Satelit" yang rapi
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable { onClick() }
    ) {
        // Lingkaran Icon
        Box(
            modifier = Modifier
                .size(50.dp) // Ukuran tombol satelit
                .shadow(8.dp, CircleShape, spotColor = shadowColor)
                .background(bgColor, CircleShape)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Label dengan background tipis agar terbaca jelas
        Surface(
            color = bgColor.copy(alpha = 0.9f),
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 2.dp,
            modifier = Modifier.height(22.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun NavBarItem(
    screen: BottomBarScreen,
    currentRoute: String?,
    activeColor: Color,
    onClick: (BottomBarScreen) -> Unit
) {
    val isSelected = currentRoute == screen.route
    val haptic = LocalHapticFeedback.current
    val inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)

    Box(
        modifier = Modifier
            .height(50.dp)
            .width(60.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!isSelected) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick(screen)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = screen.icon,
                contentDescription = screen.title,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier
                    .size(26.dp)
                    .scale(if (isSelected) 1.1f else 1.0f)
            )
            AnimatedVisibility(visible = isSelected) {
                Text(
                    text = screen.title,
                    color = activeColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}