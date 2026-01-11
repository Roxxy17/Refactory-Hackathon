package com.example.kalanacommerce.presentation.screen.dashboard.detail.payment

data class PaymentUiState(
    val isLoading: Boolean = true, // Default loading saat pertama dibuka
    val error: String? = null // Opsional, jika ada error loading WebView
)