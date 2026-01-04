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
import androidx.compose.ui.graphics.Color
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
    darkTheme: Boolean = isSystemInDarkTheme(),
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

            // 1. Set warna transparan
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            // 2. Izin menggambar di belakang system bar
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // 3. (PENTING) Matikan paksaan kontras sistem di Android 10+
            // Ini yang menyebabkan bar putih muncul di Light Mode meski sudah diset transparan
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }

            // 4. (Opsional) Support poni layar
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.attributes.layoutInDisplayCutoutMode =
                    android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }

            // 5. Atur warna ikon (Gelap/Terang)
            val insetsController = WindowCompat.getInsetsController(window, view)

            // Status Bar: Ikon gelap jika tema terang
            insetsController.isAppearanceLightStatusBars = !darkTheme

            // Nav Bar: Ikon gelap jika tema terang
            // PENTING: Ini harus true di Light Mode agar ikon hitam muncul di atas background transparan
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
