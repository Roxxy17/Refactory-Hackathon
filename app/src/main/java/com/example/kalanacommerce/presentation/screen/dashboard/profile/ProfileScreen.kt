package com.example.kalanacommerce.presentation.screen.dashboard.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import org.koin.androidx.compose.koinViewModel
import com.example.kalanacommerce.R
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import com.example.kalanacommerce.presentation.components.CustomToast
import com.example.kalanacommerce.presentation.components.LanguageSelectionDialog
import com.example.kalanacommerce.presentation.components.ToastType
import com.example.kalanacommerce.presentation.components.ThemeSelectionDialog

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onAuthAction: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToAddress: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTermsAndConditions: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user
    val isLoggedIn = user != null
    val currentThemeSetting = uiState.themeSetting

    // --- STATE UI ---
    var showThemeDialog by remember { mutableStateOf(false) }
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var toastType by remember { mutableStateOf(ToastType.Success) }

    // State untuk animasi masuk
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val scrollState = rememberScrollState()
    val backgroundColor = MaterialTheme.colorScheme.background
    val systemInDark = isSystemInDarkTheme()

    // State untuk memunculkan Dialog Bahasa
    var showLanguageDialog by remember { mutableStateOf(false) }

    // Di dalam ProfileScreen.kt

    var isInitialLoad by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(uiState.currentLanguage) {
        if (!isInitialLoad) {
            // Tampilkan toast hanya jika bahasa berubah (bukan saat aplikasi baru dibuka)
            toastMessage = if (uiState.currentLanguage == "id")
                "Bahasa diganti ke Indonesia 🇮🇩"
            else
                "Language changed to English 🇺🇸"
            toastType = ToastType.Success
            showToast = true
        }
        isInitialLoad = false
    }

// 2. LOGIC DIALOG (Pastikan pengecekan kode sama)
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = uiState.currentLanguage,
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { code ->
                if (code != uiState.currentLanguage) {
                    viewModel.setLanguage(code)
                }
                showLanguageDialog = false
            }
        )
    }

    // Logika Aktif Gelap/Terang (Gabungan Setting App & System)
    val isDarkActive = remember(currentThemeSetting, systemInDark) {
        when (currentThemeSetting) {
            ThemeSetting.LIGHT -> false
            ThemeSetting.DARK -> true
            ThemeSetting.SYSTEM -> systemInDark
        }
    }

    // --- CONFIG PARALLAX HEADER ---
    val headerHeight = 330.dp // Tinggi header
    val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }
    val topBarAlpha = (scrollState.value / (headerHeightPx * 0.5f)).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        // 1. BACKGROUND HEADER (PARALLAX)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .graphicsLayer {
                    translationY = -scrollState.value * 0.5f
                    alpha = 1f - (scrollState.value / headerHeightPx)
                }) {
            val backgroundImageRes =
                if (isDarkActive) R.drawable.profile_background_black else R.drawable.profile_background_white

            Image(
                painter = painterResource(id = backgroundImageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // --- GRADIENT FULL (TRANSPARANSI LEBIH LEBAR) ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                // 0% - 10%: Hitam transparan (Shadow Status Bar)
                                0.0f to Color.Black.copy(alpha = 0.4f),

                                // 10% - 90%: BENING (Transparent)
                                // Ini membuat gambar terlihat jelas dari atas sampai hampir bawah
                                0.3f to Color.Transparent, 0.7f to Color.Transparent,

                                // 90% - 100%: Fade ke Background Color
                                1.0f to backgroundColor
                            )
                        )
                    )
            )
        }

        // 2. KONTEN SCROLLABLE
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Spacer transparan (dikurangi sedikit agar overlap foto profil pas)
            Spacer(modifier = Modifier.height(headerHeight - 70.dp))

            // --- PROFILE INFO (Floating & Overlap) ---
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier.zIndex(2f)
            ) {
                // User Info Section
                if (isLoggedIn) {
                    UserInfoSection(user?.name ?: "User", user?.email ?: "", user?.name?.take(1) ?: "U")
                } else {
                    GuestInfoSection(onLoginClick = onAuthAction)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- ANIMATED CONTENT ---
            AnimatedVisibility(
                visible = visible, enter = slideInVertically(
                    animationSpec = tween(500), initialOffsetY = { it / 4 }) + fadeIn()
            ) {
                Column {
                    if (isLoggedIn) {
                        FloatingStatsBar(isDarkActive)
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // --- MENU: AKUN SAYA ---
                    if (isLoggedIn) {
                        SectionHeader("Akun Saya")
                        MenuCard(isDarkActive) {
                            // 1. Edit Profil
                            ColorfulMenuItem(
                                Icons.Outlined.Person,
                                Color(0xFF2196F3),
                                Color(0xFFE3F2FD),
                                "Edit Profil",
                                subtitle = "Ubah Profil",
                                onClick = onNavigateToEditProfile
                            )
                            MenuSpacer()

                            // 2. Alamat
                            ColorfulMenuItem(
                                Icons.Outlined.LocationOn,
                                Color(0xFF4CAF50),
                                Color(0xFFE8F5E9),
                                "Alamat Pengiriman",
                                subtitle = "Daftar Alamat",
                                onClick = onNavigateToAddress
                            )
                            MenuSpacer()

                            // 3. Keamanan Akun (PINDAH KE SINI)
                            ColorfulMenuItem(
                                Icons.Outlined.Lock,
                                Color(0xFFD32F2F),
                                Color(0xFFFFEBEE),
                                "Keamanan Akun",
                                subtitle = "Kata sandi & PIN",
                                onClick = { /* TODO */ }
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // --- MENU: PENGATURAN ---
                    SectionHeader("Pengaturan")
                    MenuCard(isDarkActive) {
                        // 1. Notifikasi
                        ColorfulMenuItem(
                            Icons.Outlined.Notifications,
                            Color(0xFF9C27B0),
                            Color(0xFFF3E5F5),
                            "Notifikasi",
                            subtitle = "Semua notifikasi aktif",
                            onClick = onNavigateToSettings
                        )
                        MenuSpacer()

                        // 2. Tampilan Aplikasi
                        ColorfulMenuItem(
                            icon = if (isDarkActive) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                            iconTint = Color(0xFFFFC107),
                            iconBg = Color(0xFFFFF8E1),
                            title = "Tampilan Aplikasi",
                            subtitle = when(currentThemeSetting) {
                                ThemeSetting.SYSTEM -> "Ikuti Sistem"
                                ThemeSetting.LIGHT -> "Mode Terang"
                                ThemeSetting.DARK -> "Mode Gelap"
                            },
                            onClick = { showThemeDialog = true }
                        )
                        MenuSpacer()

                        // 3. Bahasa
                        ColorfulMenuItem(
                            icon = Icons.Outlined.Language,
                            iconTint = Color(0xFF009688),
                            iconBg = Color(0xFFE0F2F1),
                            title = stringResource(R.string.language),
                            subtitle = if (uiState.currentLanguage == "id") "Bahasa Indonesia" else "English",
                            onClick = {
                                // BUKA DIALOG SAAT DIKLIK
                                showLanguageDialog = true
                            }
                        )
                        MenuSpacer()

                        // 4. Syarat Ketentuan
                        ColorfulMenuItem(
                            Icons.Outlined.Description,
                            Color(0xFF607D8B),
                            Color(0xFFECEFF1),
                            "Syarat & Ketentuan",
                            subtitle = "Kebijakan Penggunaan",
                            onClick = onNavigateToTermsAndConditions
                        )
                        MenuSpacer()

                        // 5. Pusat Bantuan (PINDAH PALING BAWAH)
                        ColorfulMenuItem(
                            Icons.Outlined.SupportAgent,
                            Color(0xFF0288D1),
                            Color(0xFFE1F5FE),
                            "Pusat Bantuan",
                            subtitle = "Hubungi Kami",
                            onClick = { /* TODO */ }
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Tombol Auth
                    AuthButton(isLoggedIn = isLoggedIn, onClick = onAuthAction)

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Versi 1.0.0 (Build 102)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    // 4. SCROLL LIMIT (DITAMBAH JADI 200.dp)
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }

        // 3. STICKY TOP BAR (IMPROVED)
        // Kita gunakan statusBarsPadding() agar tingginya dinamis mengikuti poni HP
        if (topBarAlpha > 0f) { // Hanya render jika mulai terlihat untuk performa
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .alpha(topBarAlpha)
                    .shadow(4.dp), // Shadow otomatis hilang jika alpha 0
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f), // Sedikit transparan (Glassy)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars) // PENTING: Padding otomatis sesuai status bar
                        .height(56.dp), // Tinggi standar AppBar Material Design
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if(isLoggedIn) "Profil Saya" else "Selamat Datang",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // DIALOGS & TOAST
        if (showThemeDialog) {
            ThemeSelectionDialog(
                currentSetting = currentThemeSetting,
                onDismiss = { showThemeDialog = false },
                onSelectTheme = { newSetting ->
                    viewModel.setTheme(newSetting)
                    showThemeDialog = false
                    val message = when (newSetting) {
                        ThemeSetting.SYSTEM -> "Tampilan mengikuti sistem \uD83D\uDCF1"
                        ThemeSetting.LIGHT -> "Mode Terang aktif ☀️"
                        ThemeSetting.DARK -> "Mode Gelap aktif \uD83C\uDF19"
                    }
                    toastMessage = message
                    toastType = ToastType.Info
                    showToast = true
                })
        }

        CustomToast(
            message = toastMessage,
            isVisible = showToast,
            type = toastType,
            onDismiss = { showToast = false })
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun UserInfoSection(userName: String, userEmail: String, initial: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp)
                .shadow(12.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.3f))
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    ), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = userEmail,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun GuestInfoSection(onLoginClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .size(120.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    spotColor = Color.Black.copy(alpha = 0.2f)
                )
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(3.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .clickable { onLoginClick() }) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Hai, Pengunjung!",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Masuk untuk akses fitur lengkap",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onLoginClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .height(50.dp)
                .widthIn(min = 220.dp)
                .shadow(
                    8.dp,
                    RoundedCornerShape(50),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
        ) {
            Text(
                text = "Masuk atau Daftar", style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp
                )
            )
        }
    }
}

@Composable
fun FloatingStatsBar(isDark: Boolean) {
    val borderColor = if (isDark) Color.White.copy(alpha = 0.2f) else Color.Transparent
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(100.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { /* TODO */ }
            .padding(8.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
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
fun MenuCard(isDark: Boolean, content: @Composable ColumnScope.() -> Unit) {
    val borderColor = if (isDark) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.1f)
    val elevation = if (isDark) 0.dp else 4.dp
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) { content() }
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
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title, style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold, fontSize = 15.sp
                ), color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
fun MenuSpacer() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 80.dp, end = 20.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
        thickness = 1.dp
    )
}

@Composable
fun AuthButton(isLoggedIn: Boolean, onClick: () -> Unit) {
    val containerColor =
        if (isLoggedIn) MaterialTheme.colorScheme.error.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary
    val contentColor =
        if (isLoggedIn) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimary
    val borderColor =
        if (isLoggedIn) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else Color.Transparent
    val text = if (isLoggedIn) "Keluar Aplikasi" else "Masuk Sekarang"
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor, contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}