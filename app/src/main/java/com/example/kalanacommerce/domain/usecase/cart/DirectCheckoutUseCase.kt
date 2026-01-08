package com.example.kalanacommerce.domain.usecase.cart

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.CheckoutResult
import com.example.kalanacommerce.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow

class DirectCheckoutUseCase(
    private val repository: CartRepository
) {
    operator fun invoke(productVariantId: String, quantity: Int): Flow<Resource<List<CheckoutResult>>> {
        return repository.directCheckout(productVariantId, quantity)
    }
}