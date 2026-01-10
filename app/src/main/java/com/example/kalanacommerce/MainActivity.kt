package com.example.kalanacommerce

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import com.example.kalanacommerce.data.local.datastore.LanguageManager
import com.example.kalanacommerce.data.local.datastore.ThemeManager
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import com.example.kalanacommerce.presentation.theme.KalanaCommerceTheme
import com.example.kalanacommerce.presentation.navigation.AppNavGraph
import com.example.kalanacommerce.presentation.navigation.Screen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject // IMPORT YANG BENAR
import android.graphics.Color as AndroidColor

class MainActivity : AppCompatActivity() {

    // Menggunakan delegasi 'by inject()' tanpa parameter clazz
    private val themeManager: ThemeManager by inject()
    private val languageManager: LanguageManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                AndroidColor.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                AndroidColor.TRANSPARENT,
                AndroidColor.TRANSPARENT // Ini kuncinya, scrim transparan
            )
        )
        super.onCreate(savedInstanceState)

        // Di MainActivity.kt onCreate
        lifecycleScope.launch {
            val savedLang = languageManager.languageFlow.first()
            val currentLocales = AppCompatDelegate.getApplicationLocales()

            // Gunakan toLanguageTags() untuk perbandingan string yang akurat
            if (currentLocales.toLanguageTags() != savedLang) {
                val appLocale = LocaleListCompat.forLanguageTags(savedLang)
                AppCompatDelegate.setApplicationLocales(appLocale)
            }
        }

        setContent {
            // 2. Pantau Perubahan Tema secara Real-time
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)

            val isDarkTheme = when (themeSetting) {
                ThemeSetting.LIGHT -> false
                ThemeSetting.DARK -> true
                ThemeSetting.SYSTEM -> isSystemInDarkTheme()
            }

            KalanaCommerceTheme(
                darkTheme = isDarkTheme
            ) {
                // 3. NAVIGASI UTAMA
                AppNavGraph(
                    startDestination = Screen.Dashboard.route
                )
            }
        }
    }
}