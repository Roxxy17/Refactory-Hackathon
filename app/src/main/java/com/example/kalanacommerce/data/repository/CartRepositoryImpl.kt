package com.example.kalanacommerce.data.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.mapper.toDomain
import com.example.kalanacommerce.data.remote.dto.cart.*
import com.example.kalanacommerce.data.remote.service.CartApiService
import com.example.kalanacommerce.domain.model.CartItem
import com.example.kalanacommerce.domain.model.CheckoutResult
import com.example.kalanacommerce.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CartRepositoryImpl(
    private val apiService: CartApiService
) : CartRepository {

    override fun getCartItems(): Flow<Resource<List<CartItem>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getCartItems()
            if (response.status) {
                // API mengembalikan data di dalam objek "data: { items: [] }"
                val items = response.data.items.map { it.toDomain() }
                emit(Resource.Success(items))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Terjadi kesalahan jaringan"))
        }
    }

    override fun addToCart(productVariantId: String, quantity: Int): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val request = AddToCartRequestDto(productVariantId, quantity)
            val response = apiService.addToCart(request)
            if (response.status) {
                emit(Resource.Success(response.message))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal menambahkan ke keranjang"))
        }
    }

    override fun updateCartItemQuantity(cartItemId: String, quantity: Int): Flow<Resource<String>> = flow {
        // Tidak perlu emit loading jika ingin update silent di UI, tapi best practice tetap emit
        // emit(Resource.Loading())
        try {
            val request = UpdateCartRequestDto(quantity)
            val response = apiService.updateCartItem(cartItemId, request)
            if (response.status) {
                emit(Resource.Success(response.message))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal update keranjang"))
        }
    }

    override fun deleteCartItem(cartItemId: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.deleteCartItem(cartItemId)
            if (response.status) {
                emit(Resource.Success(response.message))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal menghapus item"))
        }
    }

    override fun checkout(cartItemIds: List<String>): Flow<Resource<List<CheckoutResult>>> = flow {
        emit(Resource.Loading())
        try {
            val request = CheckoutRequestDto(cartItemIds)
            val response = apiService.checkout(request)
            if (response.status) {
                val results = response.data.map { it.toDomain() }
                emit(Resource.Success(results))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Checkout gagal"))
        }
    }

    override fun directCheckout(productVariantId: String, quantity: Int): Flow<Resource<List<CheckoutResult>>> = flow {
        emit(Resource.Loading())
        try {
            val item = DirectCheckoutItemDto(productVariantId, quantity)
            val request = DirectCheckoutRequestDto(listOf(item))
            val response = apiService.directCheckout(request)
            if (response.status) {
                val results = response.data.map { it.toDomain() }
                emit(Resource.Success(results))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Buy Now gagal"))
        }
    }
}