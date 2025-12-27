package com.example.kalanacommerce.presentation.screen.dashboard.profile

import com.example.kalanacommerce.data.remote.dto.auth.UserDto

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: UserDto? = null,
    val isDarkTheme: Boolean = false // Field ini wajib ada
)