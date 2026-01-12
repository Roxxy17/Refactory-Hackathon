package com.example.kalanacommerce.presentation.screen.dashboard.history.group

import com.example.kalanacommerce.domain.model.Order

data class TransactionGroupUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val orders: List<Order> = emptyList(), // Semua order dalam grup ini
    val paymentGroupId: String = ""
) {
    // Helper untuk menghitung total gabungan
    val totalGroupAmount: Long get() = orders.sumOf { it.totalAmount }
    val totalItemCount: Int get() = orders.sumOf { it.itemCount }
    val paymentMethod: String get() = orders.firstOrNull()?.paymentMethod ?: "-"
    val transactionDate: String get() = orders.firstOrNull()?.date ?: "-"
}