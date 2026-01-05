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
import com.example.kalanacommerce.data.mapper.toDto
import com.example.kalanacommerce.domain.model.User
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
        observeSession()
        observeTheme()
        observeLanguage()
        observeTokenAndFetch()
    }

    // --- FUNGSI REFRESH (PULL TO REFRESH) ---
    fun refreshProfile() {
        viewModelScope.launch {
            // 1. Set isRefreshing TRUE (munculkan spinner atas)
            _uiState.update { it.copy(isRefreshing = true) }

            // 2. Panggil fetch data dengan mode refresh (isPullRefresh = true)
            fetchUserProfile(isPullRefresh = true)

            // 3. Matikan isRefreshing setelah selesai (diurus di dalam fetchUserProfile atau di sini)
            // Note: Karena fetchUserProfile async di dalam scope yang sama, kita bisa matikan di sini setelah join,
            // atau biarkan flow di fetchUserProfile yang mematikannya.
            // Agar aman, kita biarkan fetchUserProfile yang mengatur state akhirnya.
        }
    }

    private fun observeTokenAndFetch() {
        viewModelScope.launch {
            sessionManager.tokenFlow.collect { token ->
                if (!token.isNullOrEmpty()) {
                    fetchUserProfile()
                } else {
                    _uiState.update {
                        it.copy(user = null, error = null, isLoading = false)
                    }
                }
            }
        }
    }

    // Update parameter: isPullRefresh
    fun fetchUserProfile(isPullRefresh: Boolean = false) {
        viewModelScope.launch {
            val token = sessionManager.tokenFlow.firstOrNull()

            if (!token.isNullOrEmpty()) {
                // Hanya tampilkan loading tengah jika BUKAN pull refresh dan data user masih kosong
                if (!isPullRefresh && _uiState.value.user == null) {
                    _uiState.update { it.copy(isLoading = true) }
                }

                profileRepository.getProfile().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val freshUserDomain: User? = result.data

                            if (freshUserDomain != null) {
                                val userDto = freshUserDomain.toDto()
                                sessionManager.saveSession(token, userDto)
                            }

                            // [MODIFIKASI DI SINI]
                            // Jika ini refresh, kirim pesan sukses
                            val msg = if (isPullRefresh) "Profil berhasil diperbarui" else null
                            // Matikan semua loading
                            _uiState.update { it.copy(isLoading = false, isRefreshing = false, error = null, successMessage = msg) }
                        }
                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(isLoading = false, isRefreshing = false, error = result.message)
                            }
                        }
                        is Resource.Loading -> { /* Loading state handled manually above */ }
                    }
                }
            } else {
                // Jika tidak ada token, pastikan refreshing mati
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    // ... (Sisa kode tidak berubah) ...
    private fun observeSession() {
        viewModelScope.launch {
            sessionManager.userFlow.collect { userDto ->
                _uiState.update { it.copy(user = userDto) }
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

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}