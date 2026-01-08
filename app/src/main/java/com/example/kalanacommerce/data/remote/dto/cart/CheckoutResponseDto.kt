package com.example.kalanacommerce.data.remote.dto.cart

import kotlinx.serialization.Serializable

@Serializable
data class CheckoutResponseDto(
    val id: String, // Bisa orderId atau groupId
    val orderCode: String,
    val totalAmount: Long,
    val snapToken: String?,
    val snapRedirectUrl: String?
)