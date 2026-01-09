package com.example.kalanacommerce.presentation.screen.dashboard.checkout

import com.example.kalanacommerce.domain.model.CartItem
import com.example.kalanacommerce.domain.model.CheckoutResult

data class CheckoutUiState(
    val isLoading: Boolean = false,
    val error: String? = null,

    // Barang yang akan dibayar
    val checkoutItems: List<CartItem> = emptyList(),

    // Alamat Pengiriman (Bisa null jika belum set)
    val selectedAddress: AddressUiModel? = null,

    // Rincian Biaya
    val shippingCost: Long = 15000, // Dummy ongkir
    val serviceFee: Long = 1000,    // Dummy biaya layanan

    // Hasil API (Snap Token)
    val checkoutResult: CheckoutResult? = null
) {
    val subtotal: Long
        get() = checkoutItems.sumOf { it.price * it.quantity }

    val totalPayment: Long
        get() = subtotal + shippingCost + serviceFee
}

// Model sederhana untuk UI (bisa diganti dengan Domain Model Address asli kamu)
data class AddressUiModel(
    val id: String,
    val name: String,
    val phone: String,
    val address: String,
    val isMain: Boolean = false
)