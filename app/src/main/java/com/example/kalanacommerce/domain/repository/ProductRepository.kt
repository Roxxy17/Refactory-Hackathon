package com.example.kalanacommerce.domain.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    // Products
    fun getProducts(): Flow<Resource<List<Product>>>
    fun getProductDetail(id: String): Flow<Resource<Product>>

    // Outlets
    fun getOutlets(): Flow<Resource<List<Outlet>>>
    fun getOutletDetail(id: String): Flow<Resource<Outlet>>

    // Categories
    fun getCategories(): Flow<Resource<List<Category>>>

    // Units
    fun getUnits(): Flow<Resource<List<MeasurementUnit>>>

    fun getCategoryDetail(id: String): Flow<Resource<Category>>
    fun getUnitDetail(id: String): Flow<Resource<MeasurementUnit>>
}