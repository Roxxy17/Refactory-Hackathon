package com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.notification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.R
import com.example.kalanacommerce.data.local.datastore.ThemeSetting

// Warna Brand
val BrandGreen = Color(0xFF43A047)
val BrandOrange = Color(0xFFF96D20)
val BrandBlue = Color(0xFF2196F3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    onBack: () -> Unit,
    themeSetting: ThemeSetting = ThemeSetting.SYSTEM
) {
    // State Notifikasi
    var isTransactionEnabled by remember { mutableStateOf(true) }
    var isPromoEnabled by remember { mutableStateOf(false) }
    var isSecurityEnabled by remember { mutableStateOf(true) }

    // --- Setup Tema (Sama seperti HistoryScreen) ---
    val systemInDark = isSystemInDarkTheme()
    val isDarkActive = remember(themeSetting, systemInDark) {
        when (themeSetting) {
            ThemeSetting.LIGHT -> false
            ThemeSetting.DARK -> true
            ThemeSetting.SYSTEM -> systemInDark
        }
    }

    // Asset & Warna
    val backgroundImage = if (isDarkActive) R.drawable.splash_background_black else R.drawable.splash_background_white
    val textColor = if (isDarkActive) Color.White else Color.Black
    val subTextColor = if (isDarkActive) Color.White.copy(0.6f) else Color.Gray

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background Image Full Screen
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Gradient Overlay (Agar teks terbaca jelas)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            if (isDarkActive) Color.Black.copy(0.4f) else Color.White.copy(0.4f),
                            Color.Transparent,
                            if (isDarkActive) Color.Black.copy(0.2f) else Color.White.copy(0.2f)
                        )
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.settings_title),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                    },
                    navigationIcon = {
                        // Tombol Back dengan style Glossy bulat
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .background(
                                    color = if (isDarkActive) Color.White.copy(0.1f) else Color.Black.copy(0.05f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.btn_back),
                                tint = textColor
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Subtitle Header
                Text(
                    text = stringResource(R.string.settings_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = subTextColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Section Title
                Text(
                    text = stringResource(R.string.settings_section_general),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = BrandGreen
                )

                // Item 1: Transaksi
                GlossySettingsItem(
                    icon = Icons.Default.NotificationsActive,
                    iconColor = BrandGreen,
                    title = stringResource(R.string.settings_notif_transaction_title),
                    subtitle = stringResource(R.string.settings_notif_transaction_desc),
                    checked = isTransactionEnabled,
                    isDark = isDarkActive,
                    onCheckedChange = { isTransactionEnabled = it }
                )

                // Item 2: Promo
                GlossySettingsItem(
                    icon = Icons.Default.LocalOffer,
                    iconColor = BrandOrange,
                    title = stringResource(R.string.settings_notif_promo_title),
                    subtitle = stringResource(R.string.settings_notif_promo_desc),
                    checked = isPromoEnabled,
                    isDark = isDarkActive,
                    onCheckedChange = { isPromoEnabled = it }
                )

                // Item 3: Keamanan (Tambahan biar lengkap)
                GlossySettingsItem(
                    icon = Icons.Default.Security,
                    iconColor = BrandBlue,
                    title = stringResource(R.string.settings_notif_account_title),
                    subtitle = stringResource(R.string.settings_notif_account_desc),
                    checked = isSecurityEnabled,
                    isDark = isDarkActive,
                    onCheckedChange = { isSecurityEnabled = it }
                )
            }
        }
    }
}

@Composable
fun GlossySettingsItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    isDark: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    // Style Glassmorphism Manual (Tanpa perlu import eksternal)
    val backgroundColor = if (isDark) Color.White.copy(0.05f) else Color.White.copy(0.7f)
    val borderColor = if (isDark) Color.White.copy(0.1f) else Color.White.copy(0.5f)
    val textColor = if (isDark) Color.White else Color.Black
    val subTextColor = if (isDark) Color.White.copy(0.6f) else Color.Gray.copy(0.8f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon Circle
            Surface(
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Texts
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    ),
                    color = textColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = subTextColor,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Switch Custom Colors
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = BrandGreen,
                    uncheckedThumbColor = if (isDark) Color.Gray else Color.White,
                    uncheckedTrackColor = if (isDark) Color(0xFF333333) else Color(0xFFE0E0E0),
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}