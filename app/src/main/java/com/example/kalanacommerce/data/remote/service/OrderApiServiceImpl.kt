package com.example.kalanacommerce.data.remote.service

import com.example.kalanacommerce.data.remote.dto.product.BaseResponse
import com.example.kalanacommerce.data.remote.dto.order.OrderDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class OrderApiServiceImpl(
    private val client: HttpClient
) : OrderApiService {

    override suspend fun getOrders(): BaseResponse<List<OrderDto>> {
        return client.get("orders").body()
    }

    override suspend fun getOrderDetail(id: String): BaseResponse<OrderDto> {
        return client.get("orders/$id").body()
    }
}