package com.example.kalanacommerce.front.screen.dashboard.profile

import androidx.compose.foundation.background
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val BackgroundLight = Color(0xFFF8F9FA) // Fallback color

/**
 * Layar utama Profile.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    isLoggedIn: Boolean,
    onAuthAction: () -> Unit, // Handler untuk Login ATAU Logout
    onNavigateToEditProfile: () -> Unit,
    onNavigateToAddress: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    // Warna tema untuk akses mudah
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profil Saya",
                        fontWeight = FontWeight.SemiBold,
                        color = primaryColor
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- 1. Header Profile ---
            // Tampilan berubah tergantung status login
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                if (isLoggedIn) {
                    ProfileHeader(
                        userName = "Sinta Dewi", // Nanti bisa diganti data dinamis dari ViewModel
                        userEmail = "sinta.dewi@example.com",
                        modifier = Modifier.padding(20.dp)
                    )
                } else {
                    ProfileHeader(
                        userName = "Tamu",
                        userEmail = "Silakan login untuk akses penuh",
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 2. Bagian Menu (Opsi Akun) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
            ) {
                // Menu Edit Profil
                ProfileMenuItem(
                    icon = Icons.Default.Person,
                    title = "Edit Profil",
                    subtitle = "Perbarui informasi akun Anda",
                    onClick = onNavigateToEditProfile
                )

                // Menu Alamat
                ProfileMenuItem(
                    icon = Icons.Default.LocationOn,
                    title = "Alamat Pengiriman",
                    subtitle = "Kelola alamat pengiriman",
                    onClick = onNavigateToAddress
                )

                // Menu Pengaturan
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Pengaturan Aplikasi",
                    subtitle = "Notifikasi dan preferensi",
                    onClick = onNavigateToSettings
                )

                // --- 3. Tombol Auth (Login / Logout) Dinamis ---
                val authTitle = if (isLoggedIn) "Logout" else "Masuk / Daftar"
                val authSubtitle = if (isLoggedIn) "Keluar dari akun" else "Masuk ke akun Anda"
                val authIcon = if (isLoggedIn) Icons.AutoMirrored.Filled.ExitToApp else Icons.AutoMirrored.Filled.Login

                // Warna: Merah jika Logout, Hijau (Primary) jika Login
                val authColor = if (isLoggedIn) errorColor else primaryColor

                ProfileMenuItem(
                    icon = authIcon,
                    title = authTitle,
                    subtitle = authSubtitle,
                    textColor = authColor,
                    iconColor = authColor,
                    onClick = onAuthAction,
                    showDivider = false // Item terakhir tidak butuh garis pembatas
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// --- Komponen Header Profil ---
@Composable
fun ProfileHeader(userName: String, userEmail: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.take(1).uppercase(), // Ambil inisial
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Info Teks
        Column {
            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF333333)
            )
            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

// --- Komponen Item Menu Reusable ---
@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    textColor: Color = Color.Black,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    showDivider: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 14.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Ikon dengan Box background tipis
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Judul dan Subtitle
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = textColor
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Panah Navigasi
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color.LightGray.copy(alpha = 0.8f),
                modifier = Modifier.size(16.dp)
            )
        }

        // Divider
        if (showDivider) {
            HorizontalDivider(
                color = Color(0xFFE0E0E0),
                thickness = 0.5.dp,
                modifier = Modifier.padding(start = 72.dp)
            )
        }
    }
}

// --- Preview ---

@Preview(showBackground = true, name = "Logged In State")
@Composable
fun ProfileScreenLoggedInPreview() {
    ProfileScreen(
        isLoggedIn = true,
        onAuthAction = {},
        onNavigateToEditProfile = {},
        onNavigateToAddress = {},
        onNavigateToSettings = {}
    )
}

@Preview(showBackground = true, name = "Guest State")
@Composable
fun ProfileScreenGuestPreview() {
    ProfileScreen(
        isLoggedIn = false,
        onAuthAction = {},
        onNavigateToEditProfile = {},
        onNavigateToAddress = {},
        onNavigateToSettings = {}
    )
}