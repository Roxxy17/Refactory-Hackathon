package com.example.kalanacommerce.front.screen.auth.forgotpassword

data class ForgotPasswordUiState(
    val email: String = "",
    val isEmailValid: Boolean = true,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)