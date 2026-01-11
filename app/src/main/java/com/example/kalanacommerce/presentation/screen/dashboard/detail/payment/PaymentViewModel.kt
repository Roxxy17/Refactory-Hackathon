package com.example.kalanacommerce.presentation.screen.dashboard.detail.payment

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PaymentViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState = _uiState.asStateFlow()

    // Dipanggil saat WebView mulai memuat halaman
    fun onPageStarted() {
        _uiState.update { it.copy(isLoading = true, error = null) }
    }

    // Dipanggil saat WebView selesai memuat halaman
    fun onPageFinished() {
        _uiState.update { it.copy(isLoading = false) }
    }

    // Opsional: Handle error loading
    fun onError(message: String) {
        _uiState.update { it.copy(isLoading = false, error = message) }
    }
}