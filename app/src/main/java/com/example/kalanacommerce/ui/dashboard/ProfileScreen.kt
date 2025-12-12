package com.example.kalanacommerce.ui.dashboard

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
/**
 * Layar utama Profile.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToAddress: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    // Warna tema untuk akses mudah
    val primaryColor = MaterialTheme.colorScheme.primary
    val darkText = Color(0xFF333333)

    Scaffold(
        topBar = {
            // Mengganti CenterAlignedTopAppBar dengan versi sederhana untuk desain yang lebih clean
            TopAppBar(
                title = { Text("Profil Saya", fontWeight = FontWeight.SemiBold, color = primaryColor) },
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

            // Header Profile dan Info Dasar DIBUNGKUS DALAM CARD UNTUK ELEVASI
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                ProfileHeader(
                    userName = "Sinta Dewi",
                    userEmail = "sinta.dewi@example.com",
                    modifier = Modifier.padding(20.dp) // Padding lebih besar
                )
            }

            Spacer(modifier = Modifier.height(24.dp)) // Jarak lebih besar antar section

            // Bagian Menu (Opsi Akun)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp)) // Corner radius lebih besar
                    .background(Color.White)
            ) {
                ProfileMenuItem(
                    icon = Icons.Default.Person,
                    title = "Edit Profil",
                    subtitle = "Perbarui informasi akun Anda",
                    onClick = onNavigateToEditProfile
                )
                ProfileMenuItem(
                    icon = Icons.Default.LocationOn,
                    title = "Alamat Pengiriman",
                    subtitle = "Kelola alamat pengiriman",
                    onClick = onNavigateToAddress
                )
                ProfileMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Pengaturan Aplikasi",
                    subtitle = "Notifikasi dan preferensi",
                    onClick = onNavigateToSettings
                )
                // Tombol Logout Dibuat menonjol
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = "Logout",
                    subtitle = "Keluar dari akun",
                    textColor = Color(0xFFD32F2F),
                    iconColor = Color(0xFFD32F2F),
                    onClick = onLogout,
                    showDivider = false // Tidak perlu divider di tombol terakhir
                )
            }
        }
    }
}

// --- Komponen Header Profil Ditingkatkan ---
@Composable
fun ProfileHeader(userName: String, userEmail: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar dengan inisial atau ikon latar belakang
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer), // Warna dari tema
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.take(1), // Ambil inisial
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

// --- Komponen Item Menu Reusable Ditingkatkan ---
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
                // Ikon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.1f)), // Latar belakang ikon lembut
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
            Divider(color = Color(0xFFE0E0E0), thickness = 0.5.dp, modifier = Modifier.padding(start = 72.dp))
        }
    }
}

// --- Preview ---

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(onLogout = {})
}