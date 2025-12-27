package com.example.kalanacommerce.presentation.screen.dashboard.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import com.example.kalanacommerce.R

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onAuthAction: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToAddress: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTermsAndConditions: () -> Unit // Callback navigasi ke T&C
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user
    val isDarkTheme = uiState.isDarkTheme
    val isLoggedIn = user != null

    val backgroundColor = MaterialTheme.colorScheme.background

    Scaffold(
        containerColor = backgroundColor,
        // Matikan insets agar gambar header bisa full screen di belakang status bar
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {

            // --- 1. BACKGROUND IMAGE HEADER ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            ) {
                val backgroundImageRes = if (isDarkTheme) {
                    R.drawable.profile_background_black
                } else {
                    R.drawable.profile_background_white
                }

                Image(
                    painter = painterResource(id = backgroundImageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.25f to Color.Transparent,
                                    1.0f to backgroundColor
                                )
                            )
                        )
                )
            }

            // --- 2. KONTEN SCROLLABLE ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(140.dp))

                // --- PROFILE CARD ---
                Box(contentAlignment = Alignment.Center) {
                    if (isLoggedIn) {
                        UserInfoSection(
                            userName = user?.name ?: "Pengguna",
                            userEmail = user?.email ?: "",
                            initial = user?.name?.take(1)?.uppercase() ?: "U"
                        )
                    } else {
                        GuestInfoSection(onLoginClick = onAuthAction)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- FLOATING STATS BAR ---
                if (isLoggedIn) {
                    FloatingStatsBar()
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // --- MENU: AKUN ---
                if (isLoggedIn) {
                    SectionHeader("Akun Saya")
                    MenuCard {
                        ColorfulMenuItem(
                            icon = Icons.Outlined.Person,
                            iconTint = Color(0xFF2196F3),
                            iconBg = Color(0xFFE3F2FD),
                            title = "Edit Profil",
                            onClick = onNavigateToEditProfile
                        )
                        MenuSpacer()
                        ColorfulMenuItem(
                            icon = Icons.Outlined.LocationOn,
                            iconTint = Color(0xFF4CAF50),
                            iconBg = Color(0xFFE8F5E9),
                            title = "Alamat Pengiriman",
                            onClick = onNavigateToAddress
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // --- MENU: PENGATURAN ---
                SectionHeader("Pengaturan")
                MenuCard {
                    // Notifikasi
                    ColorfulMenuItem(
                        icon = Icons.Outlined.Notifications,
                        iconTint = Color(0xFF9C27B0), // Ungu
                        iconBg = Color(0xFFF3E5F5),
                        title = "Notifikasi",
                        onClick = onNavigateToSettings
                    )
                    MenuSpacer()

                    // Switch Tema
                    ThemeToggleItem(isDarkTheme = isDarkTheme) { viewModel.toggleTheme(it) }
                    MenuSpacer()

                    // Bahasa
                    ColorfulMenuItem(
                        icon = Icons.Outlined.Language,
                        iconTint = Color(0xFF009688), // Teal
                        iconBg = Color(0xFFE0F2F1),
                        title = "Bahasa",
                        subtitle = "Indonesia",
                        onClick = { /* TODO: Ganti Bahasa */ }
                    )
                    MenuSpacer()

                    // --- ITEM BARU: SYARAT & KETENTUAN ---
                    ColorfulMenuItem(
                        icon = Icons.Outlined.Description, // Ikon Dokumen
                        iconTint = Color(0xFF607D8B), // Blue Grey (Warna Netral/Profesional)
                        iconBg = Color(0xFFECEFF1),   // Background Abu Sangat Muda
                        title = "Syarat & Ketentuan",
                        subtitle = "Kebijakan Penggunaan",
                        onClick = onNavigateToTermsAndConditions // Panggil Navigasi di sini
                    )
                    MenuSpacer()

                    // Bantuan Dan Laporan
                    ColorfulMenuItem(
                        icon = Icons.Outlined.Language,
                        iconTint = Color(0xFF009688), // Teal
                        iconBg = Color(0xFFE0F2F1),
                        title = "Bantuan & Laporan",
                        subtitle = "Pusat bantuan & pengaduan",
                        onClick = { /* TODO: Ganti Bahasa */ }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // --- AUTH BUTTON ---
                AuthButton(isLoggedIn = isLoggedIn, onClick = onAuthAction)

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Versi 1.0.0 (Build 102)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

// ==========================================
// COMPONENT: PROFILE INFO (GLASS EFFECT)
// ==========================================

@Composable
fun UserInfoSection(userName: String, userEmail: String, initial: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp)
                .shadow(16.dp, CircleShape)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun GuestInfoSection(onLoginClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp)
                .shadow(16.dp, CircleShape)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .clickable { onLoginClick() }
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(60.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Selamat Datang",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        TextButton(onClick = onLoginClick) {
            Text("Masuk atau Daftar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// COMPONENT: STATS BAR
// ==========================================

@Composable
fun FloatingStatsBar() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(90.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatsItem(Icons.Outlined.ShoppingBag, "Pesanan", "2", Color(0xFF2196F3))
            StatsDivider()
            StatsItem(Icons.Outlined.FavoriteBorder, "Favorit", "12", Color(0xFFE91E63))
            StatsDivider()
            StatsItem(Icons.Outlined.LocalOffer, "Voucher", "5", Color(0xFFFF9800))
        }
    }
}

@Composable
fun StatsItem(icon: ImageVector, label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { /* TODO */ }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatsDivider() {
    Box(
        modifier = Modifier
            .height(40.dp)
            .width(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    )
}

// ==========================================
// COMPONENT: MENU LIST
// ==========================================

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 28.dp, bottom = 12.dp)
    )
}

@Composable
fun MenuCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            content()
        }
    }
}

@Composable
fun ColorfulMenuItem(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(iconBg)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun ThemeToggleItem(isDarkTheme: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF263238))
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                contentDescription = null,
                tint = Color(0xFFFFCA28),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = if (isDarkTheme) "Mode Terang" else "Mode Gelap",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isDarkTheme,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
            )
        )
    }
}

@Composable
fun MenuSpacer() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 84.dp, end = 24.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
        thickness = 1.dp
    )
}

@Composable
fun AuthButton(isLoggedIn: Boolean, onClick: () -> Unit) {
    val containerColor = if (isLoggedIn) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary
    val contentColor = if (isLoggedIn) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimary
    val text = if (isLoggedIn) "Keluar Aplikasi" else "Masuk Sekarang"
    val icon = if (isLoggedIn) Icons.AutoMirrored.Filled.ExitToApp else Icons.AutoMirrored.Filled.Login

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileWithBgPreview() {
    MaterialTheme {
        ProfileScreen(
            onAuthAction = {},
            onNavigateToEditProfile = {},
            onNavigateToAddress = {},
            onNavigateToSettings = {},
            onNavigateToTermsAndConditions = {}
        )
    }
}