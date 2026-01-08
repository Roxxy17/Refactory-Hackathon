package com.example.kalanacommerce.domain.usecase.cart

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AddToCartUseCase(
    private val repository: CartRepository
) {
    operator fun invoke(productVariantId: String, quantity: Int): Flow<Resource<String>> {
        if (quantity <= 0) {
            // Validasi Bisnis sederhana di UseCase
            return flow { emit(Resource.Error("Jumlah barang minimal 1")) }
        }
        return repository.addToCart(productVariantId, quantity)
    }
}