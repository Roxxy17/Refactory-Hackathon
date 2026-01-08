package com.example.kalanacommerce.data.remote.service

import com.example.kalanacommerce.data.remote.dto.product.BaseResponse
import com.example.kalanacommerce.data.remote.dto.cart.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class CartApiServiceImpl(
    private val client: HttpClient
) : CartApiService {

    override suspend fun getCartItems(): BaseResponse<CartDataDto> {
        return client.get("carts").body()
    }

    override suspend fun addToCart(request: AddToCartRequestDto): BaseResponse<Unit> {
        return client.post("carts/items") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun updateCartItem(id: String, request: UpdateCartRequestDto): BaseResponse<Unit> {
        return client.patch("carts/items/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun deleteCartItem(id: String): BaseResponse<Unit> {
        return client.delete("carts/items/$id").body()
    }

    override suspend fun checkout(request: CheckoutRequestDto): BaseResponse<List<CheckoutResponseDto>> {
        return client.post("carts/checkout") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun directCheckout(request: DirectCheckoutRequestDto): BaseResponse<List<CheckoutResponseDto>> {
        return client.post("carts/checkout/direct") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}