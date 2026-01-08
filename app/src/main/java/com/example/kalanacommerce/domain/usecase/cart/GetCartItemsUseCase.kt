package com.example.kalanacommerce.domain.usecase.cart

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.CartItem
import com.example.kalanacommerce.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow

class GetCartItemsUseCase(
    private val repository: CartRepository
) {
    operator fun invoke(): Flow<Resource<List<CartItem>>> {
        return repository.getCartItems()
    }
}