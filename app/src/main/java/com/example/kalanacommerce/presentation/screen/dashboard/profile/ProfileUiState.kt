package com.example.kalanacommerce.presentation.screen.dashboard.profile

import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import com.example.kalanacommerce.data.remote.dto.user.ProfileUserDto

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: ProfileUserDto? = null,
    val error: String? = null,

    // Settingan Tampilan
    val isDarkTheme: Boolean = false, // Hasil kalkulasi (untuk UI render)
    val themeSetting: ThemeSetting = ThemeSetting.SYSTEM, // Pilihan user (untuk Radio Button)

    // Settingan Bahasa
    val currentLanguage: String = "id",
    val shouldShowToast: Boolean = false
)