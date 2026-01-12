package com.example.kalanacommerce.presentation.screen.dashboard.detail.checkout

import com.example.kalanacommerce.domain.model.CartItem
import com.example.kalanacommerce.domain.model.CheckoutResult

data class CheckoutUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val checkoutItems: List<CartItem> = emptyList(),
    val selectedAddress: AddressUiModel? = null,
    val availableAddresses: List<AddressUiModel> = emptyList(),
    val shippingCost: Long = 0,
    val serviceFee: Long = 0,
    val checkoutResult: CheckoutResult? = null
) {
    val subtotal: Long
        get() = checkoutItems.sumOf { it.price * it.quantity }

    val totalPayment: Long
        get() = subtotal + shippingCost + serviceFee
}

data class AddressUiModel(
    val id: String,
    val name: String,
    val phone: String,
    val address: String,
    val isMain: Boolean = false,
    val label: String? = "Alamat"
)

data class PickupTimelineState(
    val prepTime: Int = 5,
    val travelTime: Int = 5,
    val pickupTime: Int = 1,
    val totalTime: Int = 11,
    val storeCount: Int = 1 // Info tambahan untuk UI: Berapa toko yang dikunjungi
)

data class StoreLocationModel(
    val name: String,
    val address: String,
    val distance: String,
    val isMultiStore: Boolean = false
)