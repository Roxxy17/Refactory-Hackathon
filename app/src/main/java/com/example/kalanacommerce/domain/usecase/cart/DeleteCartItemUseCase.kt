package com.example.kalanacommerce.domain.usecase.cart

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow

class DeleteCartItemUseCase(
    private val repository: CartRepository
) {
    operator fun invoke(cartItemId: String): Flow<Resource<String>> {
        return repository.deleteCartItem(cartItemId)
    }
}