package com.example.kalanacommerce.presentation.screen.dashboard.cart

import com.example.kalanacommerce.domain.model.CartItem
import com.example.kalanacommerce.domain.model.CheckoutResult

data class CartUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val cartItems: List<CartItem> = emptyList(),

    // Set ID item yang dicentang user
    val selectedItemIds: Set<String> = emptySet(),

    // Hasil checkout (untuk navigasi ke payment)
    val checkoutResult: CheckoutResult? = null,
    val isCheckoutLoading: Boolean = false
) {
    // Helper untuk menghitung total harga item yang DIPILIH saja
    val selectedTotalPrice: Long
        get() = cartItems
            .filter { it.id in selectedItemIds }
            .sumOf { it.price * it.quantity }

    val totalItems: Int
        get() = cartItems.sumOf { it.quantity }
}