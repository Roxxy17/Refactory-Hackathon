package com.example.kalanacommerce.front.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.kalanacommerce.back.data.local.datastore.ThemeManager
import com.example.kalanacommerce.back.data.local.datastore.ThemeSetting
import org.koin.androidx.compose.get

// --- SKEMA WARNA GELAP ---
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColorDark,
    onPrimary = DarkBackground, // Teks hitam di atas tombol hijau pastel
    primaryContainer = PrimaryColorDark.copy(alpha = 0.3f), // Warna container soft
    onPrimaryContainer = LightTextColor,

    // Fallback Secondary ke Primary agar tidak jadi ungu
    secondary = PrimaryColorDark,
    onSecondary = DarkBackground,

    background = DarkBackground,
    onBackground = LightTextColor,

    surface = DarkSurface,
    onSurface = LightTextColor,

    error = ErrorColorDark,
    onError = DarkBackground
)

// --- SKEMA WARNA TERANG ---
private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = WhitePure, // PERBAIKAN: Teks putih di atas tombol hijau (Lebih kontras)
    primaryContainer = PrimaryColor.copy(alpha = 0.1f), // Container soft
    onPrimaryContainer = DarkTextColor,

    // Fallback Secondary ke Primary
    secondary = PrimaryColor,
    onSecondary = WhitePure,

    background = LightGray,
    onBackground = DarkTextColor,

    surface = WhitePure,
    onSurface = DarkTextColor,

    error = ErrorColor,
    onError = WhitePure
)

// TODO : Jangan lupa besok jumat 19 desember rubah semua ui agar bisa implementasi dark theme
@Composable
fun KalanaCommerceTheme(
    // Hapus parameter darkTheme, kita akan menentukannya di dalam
    content: @Composable () -> Unit
) {
    // 1. Dapatkan ThemeManager dari Koin
    val themeManager: ThemeManager = get()

    // 2. Ambil pilihan tema dari Flow, default ke SYSTEM jika belum siap
    val currentThemeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)

    // 3. Tentukan apakah mode gelap harus aktif berdasarkan pilihan
    val useDarkTheme: Boolean = when (currentThemeSetting) {
        ThemeSetting.LIGHT -> false
        ThemeSetting.DARK -> true
        ThemeSetting.SYSTEM -> isSystemInDarkTheme() // Hanya gunakan ini jika pilihan adalah SYSTEM
    }

    // 4. Pilih ColorScheme yang sesuai
    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme

    // ... (kode SideEffect untuk status bar tetap sama)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}