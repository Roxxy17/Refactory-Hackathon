package com.example.kalanacommerce.data.remote.service

import com.example.kalanacommerce.data.remote.dto.product.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class ProductApiServiceImpl(
    private val client: HttpClient
) : ProductApiService {

    override suspend fun getProducts(): BaseResponse<List<ProductDto>> {
        return client.get("products").body()
    }

    override suspend fun getProductById(id: String): BaseResponse<ProductDto> {
        return client.get("products/$id").body()
    }

    override suspend fun getOutlets(): BaseResponse<List<OutletDto>> {
        return client.get("outlets").body()
    }

    override suspend fun getOutletById(id: String): BaseResponse<OutletDto> {
        return client.get("outlets/$id").body()
    }

    override suspend fun getCategories(): BaseResponse<List<CategoryDto>> {
        return client.get("categories").body()
    }

    override suspend fun getCategoryById(id: String): BaseResponse<CategoryDto> {
        return client.get("categories/$id").body()
    }

    override suspend fun getUnits(): BaseResponse<List<UnitDto>> {
        return client.get("units").body()
    }

    override suspend fun getUnitById(id: String): BaseResponse<UnitDto> {
        return client.get("units/$id").body()
    }
}