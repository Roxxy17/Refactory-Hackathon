package com.example.kalanacommerce.presentation.screen.dashboard.history.detail

import com.example.kalanacommerce.domain.model.Order

data class DetailOrderUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val order: Order? = null,
    val defaultLat: Double = 0.0,
    val defaultLong: Double = 0.0
)