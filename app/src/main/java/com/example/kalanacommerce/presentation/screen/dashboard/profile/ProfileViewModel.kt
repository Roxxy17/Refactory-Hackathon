// File: D:/My-Project/KalanaCommerce/app/src/main/java/com/example/kalanacommerce/front/screen/dashboard/profile/ProfileViewModel.kt

package com.example.kalanacommerce.presentation.screen.dashboard.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val themeManager: ThemeManager // Inject ThemeManager
) : ViewModel() {

    // Gabungkan state user dan state theme
    val uiState: StateFlow<ProfileUiState> = combine(
        sessionManager.userFlow,
        themeManager.themeSettingFlow // Ambil flow tema
    ) { user, themeSetting ->

        // Logika konversi: Switch dianggap "ON" (True) hanya jika tema diset ke DARK
        val isDark = themeSetting == ThemeSetting.DARK

        ProfileUiState(
            user = user,
            isLoading = false,
            isDarkTheme = isDark
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState(isLoading = true)
    )

    // Fungsi Toggle dari UI (Boolean -> Enum)
    fun toggleTheme(isChecked: Boolean) {
        viewModelScope.launch {
            // Jika user nyalakan Switch -> Simpan DARK
            // Jika user matikan Switch -> Simpan LIGHT
            // (Catatan: Penggunaan Switch akan menimpa setting SYSTEM menjadi manual)
            val newSetting = if (isChecked) ThemeSetting.DARK else ThemeSetting.LIGHT
            themeManager.saveThemeSetting(newSetting)
        }
    }
}