package com.example.kalanacommerce.data.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.mapper.toDomain
import com.example.kalanacommerce.data.remote.service.ProductApiService
import com.example.kalanacommerce.domain.model.*
import com.example.kalanacommerce.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import java.io.IOException

class ProductRepositoryImpl(
    private val apiService: ProductApiService
) : ProductRepository {

    override fun getProducts(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getProducts()
            if (response.status) {
                val products = response.data.map { it.toDomain() }
                emit(Resource.Success(products))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(handleError(e))
        }
    }

    override fun getProductDetail(id: String): Flow<Resource<Product>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getProductById(id)
            if (response.status) {
                emit(Resource.Success(response.data.toDomain()))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(handleError(e))
        }
    }

    override fun getOutlets(): Flow<Resource<List<Outlet>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getOutlets()
            if (response.status) {
                val outlets = response.data.map { it.toDomain() }
                emit(Resource.Success(outlets))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(handleError(e))
        }
    }

    override fun getOutletDetail(id: String): Flow<Resource<Outlet>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getOutletById(id)
            if (response.status) {
                emit(Resource.Success(response.data.toDomain()))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(handleError(e))
        }
    }

    override fun getCategories(): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getCategories()
            if (response.status) {
                val categories = response.data.map { it.toDomain() }
                emit(Resource.Success(categories))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(handleError(e))
        }
    }

    override fun getUnits(): Flow<Resource<List<MeasurementUnit>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getUnits()
            if (response.status) {
                val units = response.data.map { it.toDomain() }
                emit(Resource.Success(units))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(handleError(e))
        }
    }

    override fun getCategoryDetail(id: String): Flow<Resource<Category>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getCategoryById(id)
            if (response.status) {
                emit(Resource.Success(response.data.toDomain()))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(handleError(e))
        }
    }

    override fun getUnitDetail(id: String): Flow<Resource<MeasurementUnit>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getUnitById(id)
            if (response.status) {
                emit(Resource.Success(response.data.toDomain()))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(handleError(e))
        }
    }

    // Helper untuk error handling
    private fun <T> handleError(e: Exception): Resource<T> {
        return when (e) {
            is ClientRequestException -> Resource.Error("Kesalahan klien: ${e.response.status.description}")
            is ServerResponseException -> Resource.Error("Kesalahan server: ${e.response.status.description}")
            is IOException -> Resource.Error("Tidak ada koneksi internet")
            else -> Resource.Error("Terjadi kesalahan: ${e.localizedMessage}")
        }
    }
}