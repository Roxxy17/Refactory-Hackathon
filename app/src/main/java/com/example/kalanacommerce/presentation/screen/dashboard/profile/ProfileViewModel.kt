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
import com.example.kalanacommerce.data.mapper.toDto // <--- [PENTING] Import Mapper
import com.example.kalanacommerce.domain.model.User // <--- [PENTING] Import Domain Model
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

    fun fetchUserProfile() {
        viewModelScope.launch {
            val token = sessionManager.tokenFlow.firstOrNull()

            if (!token.isNullOrEmpty()) {
                if (_uiState.value.user == null) {
                    _uiState.update { it.copy(isLoading = true) }
                }

                // Repository sekarang mengembalikan Resource<User> (Domain)
                profileRepository.getProfile().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val freshUserDomain: User? = result.data

                            if (freshUserDomain != null) {
                                // KONVERSI DOMAIN -> DTO UNTUK SESSION
                                val userDto = freshUserDomain.toDto()
                                sessionManager.saveSession(token, userDto)
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

    private fun observeSession() {
        viewModelScope.launch {
            // SessionManager masih menyimpan DTO, jadi UI State menerima DTO
            sessionManager.userFlow.collect { userDto ->
                _uiState.update { it.copy(user = userDto) }
            }
        }
    }

    // ... (Sisa fungsi Theme dan Language tidak berubah) ...
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