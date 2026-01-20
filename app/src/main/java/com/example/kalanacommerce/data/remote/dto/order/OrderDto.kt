package com.example.kalanacommerce.data.remote.dto.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val id: String,
    val userId: String,
    val outletId: String,
    val orderCode: String,
    val totalAmount: Long,
    val netAmount: Long,
    val status: String,
    val paymentMethod: String?,
    val paymentGroupId: String?,
    val snapToken: String?,
    val snapRedirectUrl: String?,
    val createdAt: String,
    val updatedAt: String,
    val outlet: OrderOutletDto?,
    val items: List<OrderItemDto>? = null,
    val _count: OrderCountDto? = null,
    val statusPickup: String? = "PROCESS",
)

@Serializable
data class OrderOutletDto(
    val name: String
)

@Serializable
data class OrderCountDto(
    val items: Int
)

@Serializable
data class OrderItemDto(
    val id: String,
    val orderId: String,
    val productVariantId: String,
    val quantity: Int,

    // [FIX 1] Sesuaikan nama field dengan @SerialName
    @SerialName("priceAtPurchase")
    val price: String, // API mengembalikan String "6500"

    @SerialName("subtotal")
    val totalPrice: String, // API mengembalikan String "6500"

    // [FIX 2] Ambil detail produk dari nested object 'variant'
    val variant: OrderVariantDetailDto? = null
)

// DTO Tambahan untuk menangani nested structure
@Serializable
data class OrderVariantDetailDto(
    val id: String,
    val variantName: String,
    val product: OrderProductDetailDto?
)

@Serializable
data class OrderProductDetailDto(
    val name: String,
    val image: String? = null
)



@Serializable
data class UpdateStatusRequest(
    val orderId: String,
    val status: String // "READY" atau "PICKED_UP"
)