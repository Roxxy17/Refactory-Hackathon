package com.example.kalanacommerce.domain.usecase.cart

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow

class UpdateCartItemUseCase(
    private val repository: CartRepository
) {
    operator fun invoke(cartItemId: String, newQuantity: Int): Flow<Resource<String>> {
        // Jika quantity 0, bisa diarahkan ke delete (opsional logic)
        // Disini kita asumsi hanya update jumlah
        return repository.updateCartItemQuantity(cartItemId, newQuantity)
    }
}