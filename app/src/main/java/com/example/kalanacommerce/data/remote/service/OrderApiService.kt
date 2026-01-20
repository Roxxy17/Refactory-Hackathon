package com.example.kalanacommerce.data.remote.service

import com.example.kalanacommerce.data.remote.dto.product.BaseResponse
import com.example.kalanacommerce.data.remote.dto.order.OrderDto
import com.example.kalanacommerce.data.remote.dto.order.UpdateStatusRequest

interface OrderApiService {

    // GET /orders
    suspend fun getOrders(): BaseResponse<List<OrderDto>> // Perhatikan BaseResponse generic-nya List

    // GET /orders/{id}
    suspend fun getOrderDetail(id: String): BaseResponse<OrderDto>

    suspend fun updatePickupStatus(request: UpdateStatusRequest): BaseResponse<OrderDto>
}