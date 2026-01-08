package com.example.kalanacommerce.data.remote.dto.cart

import kotlinx.serialization.Serializable

// POST /carts/items
@Serializable
data class AddToCartRequestDto(
    val productVariantId: String,
    val quantity: Int
)

// PATCH /carts/items/{id}
@Serializable
data class UpdateCartRequestDto(
    val quantity: Int
)

// POST /carts/checkout
@Serializable
data class CheckoutRequestDto(
    val cartItemIds: List<String>
)

// POST /carts/checkout/direct (Buy Now)
@Serializable
data class DirectCheckoutRequestDto(
    val items: List<DirectCheckoutItemDto>
)

@Serializable
data class DirectCheckoutItemDto(
    val productVariantId: String,
    val quantity: Int
)