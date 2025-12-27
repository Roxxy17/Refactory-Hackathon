package com.example.kalanacommerce.presentation.screen.dashboard.profile

import com.example.kalanacommerce.data.local.datastore.ThemeSetting
import com.example.kalanacommerce.data.remote.dto.auth.UserDto

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: UserDto? = null,
    val isDarkTheme: Boolean = false,
    // Menambahkan field ini agar UI bisa menampilkan status tema saat ini
    val themeSetting: ThemeSetting = ThemeSetting.SYSTEM
)