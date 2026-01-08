package com.example.kalanacommerce.presentation.screen.dashboard.history

import com.example.kalanacommerce.domain.model.Order

data class OrderHistoryUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val orders: List<Order> = emptyList(),
    val filteredOrders: List<Order> = emptyList(),
    val selectedTab: Int = 0 // 0: Proses, 1: Selesai, 2: Batal
)