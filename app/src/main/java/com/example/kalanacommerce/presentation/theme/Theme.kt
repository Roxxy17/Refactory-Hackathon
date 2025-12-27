package com.example.kalanacommerce.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- SKEMA WARNA GELAP ---
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColorDark,
    onPrimary = DarkBackground,
    primaryContainer = PrimaryColorDark.copy(alpha = 0.3f),
    onPrimaryContainer = LightTextColor,
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
    onPrimary = WhitePure,
    primaryContainer = PrimaryColor.copy(alpha = 0.1f),
    onPrimaryContainer = DarkTextColor,
    secondary = PrimaryColor,
    onSecondary = WhitePure,
    background = LightGray,
    onBackground = DarkTextColor,
    surface = WhitePure,
    onSurface = DarkTextColor,
    error = ErrorColor,
    onError = WhitePure
)

@Composable
fun KalanaCommerceTheme(
    // KEMBALIKAN PARAMETER INI
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Opsional: Dynamic color (Android 12+)
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Status bar mengikuti warna background agar terlihat menyatu
            window.statusBarColor = colorScheme.background.toArgb()
            // Icon status bar: Gelap di light mode, Terang di dark mode
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}