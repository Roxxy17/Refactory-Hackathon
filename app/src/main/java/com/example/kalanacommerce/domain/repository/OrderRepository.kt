package com.example.kalanacommerce.domain.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrders(): Flow<Resource<List<Order>>>
    fun getOrderDetail(orderId: String): Flow<Resource<Order>>
    fun updatePickupStatus(orderId: String, status: String): Flow<Resource<Order>>
}