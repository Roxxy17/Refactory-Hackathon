package com.example.kalanacommerce.presentation.screen.auth.login

data class SignInUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val userName: String? = null
)
