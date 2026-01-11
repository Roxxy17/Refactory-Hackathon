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

    override fun getProducts(query: String): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading())
        try {
            val searchParam = if (query.isBlank()) null else query
            val response = apiService.getProducts(search = searchParam)

            if (response.status) {
                // [PERBAIKAN] Safe call
                val products = response.data?.map { it.toDomain() } ?: emptyList()
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
                // [PERBAIKAN] Cek null
                val productDto = response.data
                if (productDto != null) {
                    emit(Resource.Success(productDto.toDomain()))
                } else {
                    emit(Resource.Error("Produk tidak ditemukan"))
                }
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
                // [PERBAIKAN] Safe call
                val outlets = response.data?.map { it.toDomain() } ?: emptyList()
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
                // [PERBAIKAN] Cek null
                val outletDto = response.data
                if (outletDto != null) {
                    emit(Resource.Success(outletDto.toDomain()))
                } else {
                    emit(Resource.Error("Toko tidak ditemukan"))
                }
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
                // [PERBAIKAN] Safe call
                val categories = response.data?.map { it.toDomain() } ?: emptyList()
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
                // [PERBAIKAN] Safe call
                val units = response.data?.map { it.toDomain() } ?: emptyList()
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
                // [PERBAIKAN] Cek null
                val categoryDto = response.data
                if (categoryDto != null) {
                    emit(Resource.Success(categoryDto.toDomain()))
                } else {
                    emit(Resource.Error("Kategori tidak ditemukan"))
                }
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
                // [PERBAIKAN] Cek null
                val unitDto = response.data
                if (unitDto != null) {
                    emit(Resource.Success(unitDto.toDomain()))
                } else {
                    emit(Resource.Error("Satuan tidak ditemukan"))
                }
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(handleError(e))
        }
    }

    private fun <T> handleError(e: Exception): Resource<T> {
        return when (e) {
            is ClientRequestException -> Resource.Error("Kesalahan klien: ${e.response.status.description}")
            is ServerResponseException -> Resource.Error("Kesalahan server: ${e.response.status.description}")
            is IOException -> Resource.Error("Tidak ada koneksi internet")
            else -> Resource.Error("Terjadi kesalahan: ${e.localizedMessage}")
        }
    }
}