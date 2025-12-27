package com.example.kalanacommerce.presentation.screen.auth.forgotpassword

data class ForgotPasswordUiState(
    val email: String = "",
    val isEmailValid: Boolean = true,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)