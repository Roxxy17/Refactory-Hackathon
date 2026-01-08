package com.example.kalanacommerce.data.remote.service

import com.example.kalanacommerce.data.remote.dto.product.BaseResponse
import com.example.kalanacommerce.data.remote.dto.cart.*

interface CartApiService {

    // GET /carts
    suspend fun getCartItems(): BaseResponse<CartDataDto>

    // POST /carts/items (Add to Cart)
    suspend fun addToCart(request: AddToCartRequestDto): BaseResponse<Unit> // Data kosong {}

    // PATCH /carts/items/{id} (Update Qty)
    suspend fun updateCartItem(id: String, request: UpdateCartRequestDto): BaseResponse<Unit>

    // DELETE /carts/items/{id}
    suspend fun deleteCartItem(id: String): BaseResponse<Unit>

    // POST /carts/checkout
    suspend fun checkout(request: CheckoutRequestDto): BaseResponse<List<CheckoutResponseDto>>

    // POST /carts/checkout/direct
    suspend fun directCheckout(request: DirectCheckoutRequestDto): BaseResponse<List<CheckoutResponseDto>>
}