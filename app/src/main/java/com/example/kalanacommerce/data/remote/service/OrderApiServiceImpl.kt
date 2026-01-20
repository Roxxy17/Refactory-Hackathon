package com.example.kalanacommerce.data.remote.service

import com.example.kalanacommerce.data.remote.dto.product.BaseResponse
import com.example.kalanacommerce.data.remote.dto.order.OrderDto
import com.example.kalanacommerce.data.remote.dto.order.UpdateStatusRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

class OrderApiServiceImpl(
    private val client: HttpClient
) : OrderApiService {

    override suspend fun getOrders(): BaseResponse<List<OrderDto>> {
        return client.get("orders").body()
    }

    override suspend fun getOrderDetail(id: String): BaseResponse<OrderDto> {
        return client.get("orders/$id").body()
    }

    override suspend fun updatePickupStatus(request: UpdateStatusRequest): BaseResponse<OrderDto> {
        return client.patch("orders/item/status") {
            // [WAJIB TAMBAH BARIS INI]
            contentType(ContentType.Application.Json)

            setBody(request)
        }.body()
    }
}