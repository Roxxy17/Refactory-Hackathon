package com.example.kalanacommerce.presentation.screen.auth.register

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false
)
