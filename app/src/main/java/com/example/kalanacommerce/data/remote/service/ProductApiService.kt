package com.example.kalanacommerce.data.remote.service

import com.example.kalanacommerce.data.remote.dto.product.*

interface ProductApiService {
    suspend fun getProducts(): BaseResponse<List<ProductDto>>
    suspend fun getProductById(id: String): BaseResponse<ProductDto>

    suspend fun getOutlets(): BaseResponse<List<OutletDto>>
    suspend fun getOutletById(id: String): BaseResponse<OutletDto>

    suspend fun getCategories(): BaseResponse<List<CategoryDto>>
    suspend fun getCategoryById(id: String): BaseResponse<CategoryDto>

    suspend fun getUnits(): BaseResponse<List<UnitDto>>
    suspend fun getUnitById(id: String): BaseResponse<UnitDto>
}