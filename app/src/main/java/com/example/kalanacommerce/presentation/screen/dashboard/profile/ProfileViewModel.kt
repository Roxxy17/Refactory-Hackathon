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
    private val profileRepository: ProfileRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // 1. Observasi Data User Lokal (Session)
        observeSession()

        // 2. Observasi Pengaturan
        observeTheme()
        observeLanguage()

        // --- PERBAIKAN DI SINI ---
        // HAPUS: fetchUserProfile() <-- Jangan panggil manual di init!
        // GANTI DENGAN INI:
        observeTokenAndFetch()
    }

    // --- FUNGSI BARU: Pantau Token & Fetch Otomatis ---
    private fun observeTokenAndFetch() {
        viewModelScope.launch {
            // Kita collect tokenFlow. Setiap kali token berubah (Login/Logout), blok ini jalan.
            sessionManager.tokenFlow.collect { token ->
                if (!token.isNullOrEmpty()) {
                    // Ada Token (User B Login) -> Tarik data dari API
                    fetchUserProfile()
                } else {
                    // Token Null (Logout) -> Bersihkan UI State agar data hantu hilang
                    _uiState.update {
                        it.copy(user = null, error = null, isLoading = false)
                    }
                }
            }
        }
    }

    // Fungsi fetchUserProfile (sedikit penyesuaian agar lebih aman)
    fun fetchUserProfile() {
        viewModelScope.launch {
            val token = sessionManager.tokenFlow.firstOrNull()

            if (!token.isNullOrEmpty()) {
                // Tampilkan loading hanya jika data user belum ada (biar smooth)
                if (_uiState.value.user == null) {
                    _uiState.update { it.copy(isLoading = true) }
                }

                profileRepository.getProfile().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val freshUser = result.data
                            if (freshUser != null) {
                                // Simpan ke session, UI akan update via 'observeSession'
                                sessionManager.saveSession(token, freshUser)
                            }
                            _uiState.update { it.copy(isLoading = false, error = null) }
                        }
                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(isLoading = false, error = result.message)
                            }
                        }
                        is Resource.Loading -> { /* Handle loading if needed */ }
                    }
                }
            }
        }
    }

    // --- SISANYA TETAP SAMA ---
    private fun observeSession() {
        viewModelScope.launch {
            sessionManager.userFlow.collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }
    }

    private fun observeTheme() {
        viewModelScope.launch {
            themeManager.themeSettingFlow.collect { setting ->
                val isDark = calculateIsDark(setting)
                _uiState.update { it.copy(themeSetting = setting, isDarkTheme = isDark) }
            }
        }
    }

    private fun observeLanguage() {
        viewModelScope.launch {
            launch {
                languageManager.languageFlow.collect { lang ->
                    _uiState.update { it.copy(currentLanguage = lang) }
                }
            }
            launch {
                languageManager.shouldShowToastFlow.collect { show ->
                    _uiState.update { it.copy(shouldShowToast = show) }
                }
            }
        }
    }

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

    fun setTheme(newSetting: ThemeSetting) {
        viewModelScope.launch { themeManager.saveThemeSetting(newSetting) }
    }

    fun setLanguage(code: String) {
        viewModelScope.launch { languageManager.setLanguage(code) }
    }

    fun clearLangToast() {
        viewModelScope.launch { languageManager.clearPendingToast() }
    }
}