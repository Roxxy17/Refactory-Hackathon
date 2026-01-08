package com.example.kalanacommerce.data.remote.dto.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderListResponseDto(
    val data: List<OrderDto>
)

@Serializable
data class OrderDetailResponseDto(
    val data: OrderDto
)