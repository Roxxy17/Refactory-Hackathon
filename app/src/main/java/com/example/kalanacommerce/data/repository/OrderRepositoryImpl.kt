package com.example.kalanacommerce.data.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.mapper.toDomain
import com.example.kalanacommerce.data.remote.dto.order.UpdateStatusRequest
import com.example.kalanacommerce.data.remote.service.OrderApiService
import com.example.kalanacommerce.domain.model.Order
import com.example.kalanacommerce.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrderRepositoryImpl(
    private val apiService: OrderApiService
) : OrderRepository {

    override fun getOrders(): Flow<Resource<List<Order>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getOrders()
            if (response.status) {
                // [PERBAIKAN] Safe call ?.map dan elvis operator ?:
                val orders = response.data?.map { it.toDomain() } ?: emptyList()
                emit(Resource.Success(orders))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal memuat riwayat pesanan"))
        }
    }

    override fun getOrderDetail(orderId: String): Flow<Resource<Order>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getOrderDetail(orderId)
            if (response.status) {
                // [PERBAIKAN] Cek null secara manual karena ini Single Object
                val orderDto = response.data
                if (orderDto != null) {
                    emit(Resource.Success(orderDto.toDomain()))
                } else {
                    emit(Resource.Error("Data pesanan tidak ditemukan"))
                }
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal memuat detail pesanan"))
        }
    }

    override fun updatePickupStatus(orderId: String, status: String): Flow<Resource<Order>> = flow {
        emit(Resource.Loading())
        try {
            val request = UpdateStatusRequest(orderId, status)
            val response = apiService.updatePickupStatus(request)
            if (response.status && response.data != null) {
                emit(Resource.Success(response.data.toDomain()))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error updating status"))
        }
    }

}