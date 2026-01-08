package com.example.kalanacommerce.data.remote.dto.cart

import com.example.kalanacommerce.data.remote.dto.product.ProductDto
import com.example.kalanacommerce.data.remote.dto.product.ProductVariantDto
import kotlinx.serialization.Serializable

@Serializable
data class CartDataDto(
    val items: List<CartItemDto>
)

@Serializable
data class CartItemDto(
    val id: String,
    val userId: String,
    val productVariantId: String,
    val quantity: Int,
    val note: String? = null,
    val createdAt: String?,
    val updatedAt: String?,
    // Nested objects dari API (biasanya di-include)
    val variant: ProductVariantDto?,
    val product: ProductDto?
)