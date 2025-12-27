package com.example.kalanacommerce

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.kalanacommerce.data.local.datastore.ThemeManager
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import com.example.kalanacommerce.presentation.theme.KalanaCommerceTheme
import com.example.kalanacommerce.presentation.navigation.AppNavGraph
import com.example.kalanacommerce.presentation.navigation.Screen
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val themeManager: ThemeManager by inject()
    // SessionManager tidak perlu di-inject di sini untuk logic startDestination

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 1. Pantau Tema
            val themeSetting by themeManager.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)
            val isDarkTheme = when (themeSetting) {
                ThemeSetting.LIGHT -> false
                ThemeSetting.DARK -> true
                ThemeSetting.SYSTEM -> isSystemInDarkTheme()
            }

            KalanaCommerceTheme(
                darkTheme = isDarkTheme
            ) {
                // 2. SELALU MULAI DARI DASHBOARD
                // Dashboard nanti yang akan mengecek apakah user Guest atau Member
                AppNavGraph(
                    startDestination = Screen.Dashboard.route
                )
            }
        }
    }
}