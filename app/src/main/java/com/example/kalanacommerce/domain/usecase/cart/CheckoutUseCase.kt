package com.example.kalanacommerce.domain.usecase.cart

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.CheckoutResult
import com.example.kalanacommerce.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CheckoutUseCase(
    private val repository: CartRepository
) {
    operator fun invoke(cartItemIds: List<String>): Flow<Resource<List<CheckoutResult>>> {
        if (cartItemIds.isEmpty()) {
            return flow { emit(Resource.Error("Pilih minimal satu barang untuk checkout")) }
        }
        return repository.checkout(cartItemIds)
    }
}