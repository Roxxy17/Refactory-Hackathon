package com.example.kalanacommerce.data.remote.dto.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartDataDto(
    val cartId: String? = null,
    val items: List<CartItemDto>
)

@Serializable
data class CartItemDto(
    val id: String,
    val quantity: Int,
    val variant: CartVariantDto? = null
)

@Serializable
data class CartVariantDto(
    val id: String,
    val variantName: String,
    val price: String, // API mengirim string "18000"
    val product: CartProductDto? = null
)

@Serializable
data class CartProductDto(
    val id: String,
    val name: String,
    val image: String? // [FIX] Ubah jadi Nullable
)