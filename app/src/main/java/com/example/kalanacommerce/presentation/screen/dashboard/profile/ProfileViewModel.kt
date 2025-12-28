package com.example.kalanacommerce.presentation.screen.dashboard.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.data.local.datastore.LanguageManager
import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.data.local.datastore.ThemeManager
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val sessionManager: SessionManager,
    private val themeManager: ThemeManager,
    private val languageManager: LanguageManager,
    private val context: Context
) : ViewModel() {

    // Logika awal untuk menentukan apakah gelap/terang saat pertama kali load
    private val initialIsDarkTheme =
        themeManager.themeSettingFlow.value == ThemeSetting.DARK ||
                (themeManager.themeSettingFlow.value == ThemeSetting.SYSTEM &&
                        context.resources.configuration.uiMode and
                        android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                        android.content.res.Configuration.UI_MODE_NIGHT_YES)

    val uiState: StateFlow<ProfileUiState> =
        combine(
            sessionManager.userFlow,
            themeManager.themeSettingFlow,
            languageManager.languageFlow
        ) { user, themeSetting, lang ->

            // Tentukan apakah UI harus render Dark Mode atau Light Mode
            val isDark = when (themeSetting) {
                ThemeSetting.LIGHT -> false
                ThemeSetting.DARK -> true
                ThemeSetting.SYSTEM -> {
                    // Cek konfigurasi HP user saat ini
                    context.resources.configuration.uiMode and
                            android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                            android.content.res.Configuration.UI_MODE_NIGHT_YES
                }
            }

            ProfileUiState(
                user = user,
                isLoading = false,
                isDarkTheme = isDark,
                themeSetting = themeSetting,
                currentLanguage = lang// Update field ini
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileUiState(
                isLoading = true,
                isDarkTheme = initialIsDarkTheme,
                themeSetting = themeManager.themeSettingFlow.value
            )
        )

    // Fungsi baru: Set tema spesifik dari Dialog
    fun setTheme(newSetting: ThemeSetting) {
        viewModelScope.launch {
            themeManager.saveThemeSetting(newSetting)
        }
    }

    // Fungsi ganti bahasa
    fun setLanguage(code: String) {
        viewModelScope.launch {
            languageManager.setLanguage(code)
        }
    }
}