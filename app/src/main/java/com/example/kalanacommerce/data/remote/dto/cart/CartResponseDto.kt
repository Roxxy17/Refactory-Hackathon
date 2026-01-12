package com.example.kalanacommerce.data.remote.dto.cart

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
    val variant: CartVariantDto
)

@Serializable
data class CartVariantDto(
    val id: String,
    val variantName: String? = null, // Bisa null atau string
    val price: String, // API mengirim harga dalam bentuk String
    val originalPrice: String? = null, // [BARU] Harga sebelum diskon (nullable)
    val product: CartProductDto,
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
    val image: String? = null,
    val freshnessLevel: Int? = 100, // [BARU] Tingkat kesegaran dari API
    val outlet: CartOutletDto // [BARU] Data toko pemilik produk
)

@Serializable
data class CartOutletDto(
    val id: String,
    val name: String
    // lat & long bisa ditambahkan nanti jika butuh hitung jarak di cart
)