package com.example.kalanacommerce.presentation.screen.dashboard.product

import com.example.kalanacommerce.domain.model.Product
import com.example.kalanacommerce.domain.model.ProductVariant

data class DetailProductUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val product: Product? = null,
    // [BARU] Varian yang sedang dipilih
    val selectedVariant: ProductVariant? = null,
    // [BARU] Produk rekomendasi (Related)
    val relatedProducts: List<Product> = emptyList(),
    val quantity: Int = 1,
    val totalPrice: Long = 0L
)