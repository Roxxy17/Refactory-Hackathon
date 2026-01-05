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
import androidx.compose.ui.platform.LocalContext
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
import com.example.kalanacommerce.presentation.components.PullToRefreshWrapper // Import Wrapper
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onAuthAction: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToAddress: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTermsAndConditions: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToHelpCenter : () -> Unit
) {

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user
    val isLoggedIn = user != null
    val currentThemeSetting = uiState.themeSetting

    // --- STATE UI ---
    var showThemeDialog by remember { mutableStateOf(false) }
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var toastType by remember { mutableStateOf(ToastType.Success) }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val scrollState = rememberScrollState()
    val backgroundColor = MaterialTheme.colorScheme.background
    val systemInDark = isSystemInDarkTheme()

    var showLanguageDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.shouldShowToast, uiState.currentLanguage) {
        if (uiState.shouldShowToast) {
            kotlinx.coroutines.delay(100)
            toastMessage = if (uiState.currentLanguage == "id")
                "Bahasa diganti ke Indonesia 🇮🇩"
            else
                "Language changed to English 🇺🇸"
            toastType = ToastType.Success
            showToast = true
            viewModel.clearLangToast()
        }
    }

    // [TAMBAHAN] Handler untuk Success Message dari Refresh
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            toastMessage = uiState.successMessage!!
            toastType = ToastType.Success
            showToast = true
            // Delay sedikit lalu bersihkan pesan di ViewModel agar tidak muncul lagi
            delay(2000)
            viewModel.clearMessages() // Pastikan buat fungsi ini di ViewModel
        }
    }

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

    val isDarkActive = remember(currentThemeSetting, systemInDark) {
        when (currentThemeSetting) {
            ThemeSetting.LIGHT -> false
            ThemeSetting.DARK -> true
            ThemeSetting.SYSTEM -> systemInDark
        }
    }

    val headerHeight = 330.dp
    val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }
    val topBarAlpha = (scrollState.value / (headerHeightPx * 0.5f)).coerceIn(0f, 1f)

    // --- IMPLEMENTASI PULL TO REFRESH ---
    // Kita bungkus seluruh Box utama agar bisa ditarik
    PullToRefreshWrapper(
        isRefreshing = uiState.isRefreshing, // Pastikan uiState punya isRefreshing
        onRefresh = { viewModel.refreshProfile() } // Pastikan ViewModel punya fungsi refreshProfile
    ) {
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

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.0f to Color.Black.copy(alpha = 0.4f),
                                    0.3f to Color.Transparent, 0.7f to Color.Transparent,
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
                Spacer(modifier = Modifier.height(headerHeight - 70.dp))

                Box(
                    contentAlignment = Alignment.Center, modifier = Modifier.zIndex(2f)
                ) {
                    if (isLoggedIn) {
                        UserInfoSection(
                            userName = user?.name ?: "User",
                            userEmail = user?.email ?: "",
                            initial = user?.name?.take(1) ?: "U",
                            imageUrl = user?.image
                        )
                    } else {
                        GuestInfoSection(onLoginClick = onAuthAction)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = visible, enter = slideInVertically(
                        animationSpec = tween(500), initialOffsetY = { it / 4 }) + fadeIn()
                ) {
                    Column {
                        if (isLoggedIn) {
                            FloatingStatsBar(isDarkActive)
                            Spacer(modifier = Modifier.height(32.dp))
                        }

                        if (isLoggedIn) {
                            SectionHeader(stringResource(R.string.my_account))
                            MenuCard(isDarkActive) {
                                ColorfulMenuItem(
                                    Icons.Outlined.Person,
                                    Color(0xFF2196F3),
                                    Color(0xFFE3F2FD),
                                    stringResource(R.string.edit_profil),
                                    subtitle = stringResource(R.string.ubah_profil),
                                    onClick = onNavigateToEditProfile
                                )
                                MenuSpacer()
                                ColorfulMenuItem(
                                    Icons.Outlined.LocationOn,
                                    Color(0xFF4CAF50),
                                    Color(0xFFE8F5E9),
                                    stringResource(R.string.alamat_pengiriman),
                                    subtitle = stringResource(R.string.daftar_alamat),
                                    onClick = onNavigateToAddress
                                )
                                MenuSpacer()
                                ColorfulMenuItem(
                                    Icons.Outlined.Lock,
                                    Color(0xFFD32F2F),
                                    Color(0xFFFFEBEE),
                                    stringResource(R.string.keamanan_akun),
                                    subtitle = stringResource(R.string.kata_sandi_pin),
                                    onClick = onNavigateToForgotPassword
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        SectionHeader(stringResource(R.string.pengaturan))
                        MenuCard(isDarkActive) {
                            ColorfulMenuItem(
                                Icons.Outlined.Notifications,
                                Color(0xFF9C27B0),
                                Color(0xFFF3E5F5),
                                stringResource(R.string.notifikasi),
                                subtitle = stringResource(R.string.semua_notifikasi_aktif),
                                onClick = onNavigateToSettings
                            )
                            MenuSpacer()
                            ColorfulMenuItem(
                                icon = if (isDarkActive) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                                iconTint = Color(0xFFFFC107),
                                iconBg = Color(0xFFFFF8E1),
                                title = stringResource(R.string.tampilan_aplikasi),
                                subtitle = when(currentThemeSetting) {
                                    ThemeSetting.SYSTEM -> stringResource(R.string.ikuti_sistem)
                                    ThemeSetting.LIGHT -> stringResource(R.string.mode_terang)
                                    ThemeSetting.DARK -> stringResource(R.string.mode_gelap)
                                },
                                onClick = { showThemeDialog = true }
                            )
                            MenuSpacer()
                            ColorfulMenuItem(
                                icon = Icons.Outlined.Language,
                                iconTint = Color(0xFF009688),
                                iconBg = Color(0xFFE0F2F1),
                                title = stringResource(R.string.language),
                                subtitle = if (uiState.currentLanguage == "id") stringResource(R.string.bahasa_indonesia) else stringResource(R.string.english),
                                onClick = { showLanguageDialog = true }
                            )
                            MenuSpacer()
                            ColorfulMenuItem(
                                Icons.Outlined.Description,
                                Color(0xFF607D8B),
                                Color(0xFFECEFF1),
                                stringResource(R.string.syarat_ketentuan),
                                subtitle = stringResource(R.string.kebijakan_penggunaan),
                                onClick = onNavigateToTermsAndConditions
                            )
                            MenuSpacer()
                            ColorfulMenuItem(
                                Icons.Outlined.SupportAgent,
                                Color(0xFF0288D1),
                                Color(0xFFE1F5FE),
                                stringResource(R.string.pusat_bantuan),
                                subtitle = stringResource(R.string.hubungi_kami),
                                onClick = onNavigateToHelpCenter
                            )
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                        AuthButton(isLoggedIn = isLoggedIn, onClick = onAuthAction)
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = stringResource(R.string.versi_1_0_0_build_102),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(120.dp))
                    }
                }
            }

            // 3. STICKY TOP BAR
            if (topBarAlpha > 0f) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .alpha(topBarAlpha)
                        .shadow(4.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .height(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if(isLoggedIn) stringResource(R.string.profil_saya) else stringResource(R.string.selamat_datang),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Dialogs & Toast
            if (showThemeDialog) {
                ThemeSelectionDialog(
                    currentSetting = currentThemeSetting,
                    onDismiss = { showThemeDialog = false },
                    onSelectTheme = { newSetting ->
                        viewModel.setTheme(newSetting)
                        showThemeDialog = false
                        val message = when (newSetting) {
                            ThemeSetting.SYSTEM -> context.getString(R.string.tampilan_mengikuti_sistem)
                            ThemeSetting.LIGHT -> context.getString(R.string.mode_terang_aktif)
                            ThemeSetting.DARK -> context.getString(R.string.mode_gelap_aktif)
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
}

// --- SUB-COMPONENTS ---

@Composable
fun UserInfoSection(
    userName: String,
    userEmail: String,
    initial: String,
    imageUrl: String? // Tambah parameter ini
) {
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
            if (!imageUrl.isNullOrEmpty()) {
                // 1. TAMPILKAN FOTO JIKA ADA
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // 2. TAMPILKAN INISIAL JIKA FOTO KOSONG
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
            text = stringResource(R.string.hai_pengunjung),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.masuk_untuk_akses_fitur_lengkap),
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
                text = stringResource(R.string.masuk_atau_daftar), style = MaterialTheme.typography.titleMedium.copy(
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
            StatsItem(Icons.Outlined.ShoppingBag,
                stringResource(R.string.pesanan), "2", Color(0xFF2196F3))
            StatsDivider()
            StatsItem(Icons.Outlined.FavoriteBorder,
                stringResource(R.string.favorit), "12", Color(0xFFE91E63))
            StatsDivider()
            StatsItem(Icons.Outlined.LocalOffer,
                stringResource(R.string.voucher), "5", Color(0xFFFF9800))
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
    val text = if (isLoggedIn) stringResource(R.string.keluar_aplikasi) else stringResource(R.string.masuk_sekarang)
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