package com.example.kalanacommerce.domain.usecase.order

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.Order
import com.example.kalanacommerce.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class GetOrdersUseCase(
    private val repository: OrderRepository
) {
    operator fun invoke(): Flow<Resource<List<Order>>> {
        return repository.getOrders()
    }
}