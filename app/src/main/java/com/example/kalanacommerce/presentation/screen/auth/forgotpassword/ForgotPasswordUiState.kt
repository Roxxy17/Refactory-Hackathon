package com.example.kalanacommerce.presentation.screen.auth.forgotpassword

data class ForgotPasswordUiState(
    // Step 1: Email
    val email: String = "",
    val isEmailValid: Boolean = true,

    // Step 2: OTP & Reset
    val otp: String = "",               // <--- Tambahkan ini
    val newPassword: String = "",       // <--- Tambahkan ini
    val isResetSuccess: Boolean = false,// <--- Tambahkan ini

    // Umum
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,     // Sukses kirim email
    val error: String? = null
)