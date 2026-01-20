package com.example.kalanacommerce.domain.usecase.order

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.Order
import com.example.kalanacommerce.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class UpdatePickupStatusUseCase(private val repository: OrderRepository) {
    operator fun invoke(orderId: String, status: String): Flow<Resource<Order>> {
        return repository.updatePickupStatus(orderId, status)
    }
}