package com.example.kalanacommerce.presentation.screen.dashboard.profile

import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.local.datastore.LanguageManager
import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.data.local.datastore.ThemeManager
import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import com.example.kalanacommerce.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val sessionManager: SessionManager,
    private val themeManager: ThemeManager,
    private val languageManager: LanguageManager,
    private val profileRepository: ProfileRepository, // Tambahan Wajib
    private val context: Context
) : ViewModel() {

    // Kita gunakan MutableStateFlow agar bisa di-update dari berbagai sumber (Session, Theme, API)
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // 1. Observasi Data User Lokal (Session)
        observeSession()

        // 2. Observasi Pengaturan (Tema & Bahasa)
        observeTheme()
        observeLanguage()

        // 3. FETCH DATA TERBARU DARI SERVER (Solusi Bug Foto/Nama tidak update)
        fetchUserProfile()
    }

    // --- LOGIKA DATA USER ---

    private fun observeSession() {
        viewModelScope.launch {
            sessionManager.userFlow.collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            // Cek token dulu
            val token = sessionManager.tokenFlow.firstOrNull()

            if (!token.isNullOrEmpty()) {
                // Set loading true HANYA jika data user lokal masih kosong (biar gak kedip)
                if (_uiState.value.user == null) {
                    _uiState.update { it.copy(isLoading = true) }
                }

                // Panggil API
                profileRepository.getProfile().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val freshUser = result.data
                            if (freshUser != null) {
                                // Update SessionManager -> Ini akan otomatis trigger 'observeSession'
                                sessionManager.saveSession(token, freshUser)
                            }
                            _uiState.update { it.copy(isLoading = false, error = null) }
                        }
                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(isLoading = false, error = result.message)
                            }
                        }
                        is Resource.Loading -> {
                            // Loading state diurus di atas atau di sini opsional
                        }
                    }
                }
            }
        }
    }

    // --- LOGIKA TEMA & BAHASA ---

    private fun observeTheme() {
        viewModelScope.launch {
            themeManager.themeSettingFlow.collect { setting ->
                val isDark = calculateIsDark(setting)
                _uiState.update {
                    it.copy(
                        themeSetting = setting,
                        isDarkTheme = isDark
                    )
                }
            }
        }
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            // Collect Bahasa
            launch {
                languageManager.languageFlow.collect { lang ->
                    _uiState.update { it.copy(currentLanguage = lang) }
                }
            }
            // Collect Toast (Notifikasi ganti bahasa)
            launch {
                languageManager.shouldShowToastFlow.collect { show ->
                    _uiState.update { it.copy(shouldShowToast = show) }
                }
            }
        }
    }

    // Helper untuk cek mode gelap/terang
    private fun calculateIsDark(setting: ThemeSetting): Boolean {
        return when (setting) {
            ThemeSetting.LIGHT -> false
            ThemeSetting.DARK -> true
            ThemeSetting.SYSTEM -> {
                val uiMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                uiMode == Configuration.UI_MODE_NIGHT_YES
            }
        }
    }

    // --- ACTIONS ---

    fun setTheme(newSetting: ThemeSetting) {
        viewModelScope.launch {
            themeManager.saveThemeSetting(newSetting)
        }
    }

    fun setLanguage(code: String) {
        viewModelScope.launch {
            languageManager.setLanguage(code)
        }
    }

    fun clearLangToast() {
        viewModelScope.launch {
            languageManager.clearPendingToast()
        }
    }
}