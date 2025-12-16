package com.example.kalanacommerce.front.screen.auth.login

import com.example.kalanacommerce.back.domain.model.User

data class SignInUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
