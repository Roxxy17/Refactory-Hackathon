package com.example.kalanacommerce.data.remote.dto.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartResponseDto(
    val status: Boolean,
    val statusCode: Int,
    val message: String,
    val data: CartDataDto
)

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
    val originalPrice: String? = null,
    val product: CartProductDto? = null,
    val unit: CartUnitDto? = null
)

@Serializable
data class CartUnitDto(
    val name: String
)

@Serializable
data class CartProductDto(
    val id: String,
    val name: String,
    // Pastikan backend mengirim ini nanti
    val freshnessLevel: Int? = null,
    val image: String? = null,

    // [TAMBAHKAN INI] Untuk membaca nama toko asli
    val outlet: CartOutletDto? = null
)

@Serializable
data class CartOutletDto(
    val id: String,
    val name: String
)

