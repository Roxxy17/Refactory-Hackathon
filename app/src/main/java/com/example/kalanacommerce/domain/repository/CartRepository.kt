package com.example.kalanacommerce.domain.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.CartItem
import com.example.kalanacommerce.domain.model.CheckoutResult
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<Resource<List<CartItem>>>

    fun addToCart(productVariantId: String, quantity: Int): Flow<Resource<String>> // Return Success Message

    fun updateCartItemQuantity(cartItemId: String, quantity: Int): Flow<Resource<String>>

    fun deleteCartItem(cartItemId: String): Flow<Resource<String>>

    fun checkout(cartItemIds: List<String>): Flow<Resource<List<CheckoutResult>>>

    fun directCheckout(productVariantId: String, quantity: Int): Flow<Resource<List<CheckoutResult>>>
}