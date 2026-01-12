package com.example.kalanacommerce.presentation.screen.dashboard.history

import com.example.kalanacommerce.domain.model.Order

data class OrderHistoryUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val orders: List<Order> = emptyList(),
    // Ubah tipe list ini menjadi List<HistoryUiItem>
    val historyItems: List<HistoryUiItem> = emptyList(),
    val selectedTab: Int = 0
)

// Wrapper untuk membedakan tampilan di UI
sealed class HistoryUiItem {
    data class Single(val order: Order) : HistoryUiItem()
    data class Group(val paymentGroupId: String, val orders: List<Order>) : HistoryUiItem()
}