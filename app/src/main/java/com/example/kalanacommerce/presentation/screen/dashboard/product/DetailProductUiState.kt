package com.example.kalanacommerce.presentation.screen.dashboard.product

import com.example.kalanacommerce.domain.model.Product
import com.example.kalanacommerce.domain.model.ProductVariant

data class DetailProductUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val product: Product? = null,

    // Pilihan User
    val selectedVariant: ProductVariant? = null,
    val quantity: Int = 1,

    // Status Add to Cart
    val isAddToCartLoading: Boolean = false,
    val addToCartSuccessMessage: String? = null, // Untuk Trigger Toast
    val navigateToCheckoutWithId: String? = null, // Untuk Trigger Navigasi Buy Now

    val relatedProducts: List<Product> = emptyList()
) {
    val totalPrice: Long
        get() = (selectedVariant?.price ?: product?.price ?: 0L) * quantity
}