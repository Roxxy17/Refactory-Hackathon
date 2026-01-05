package com.example.kalanacommerce.domain.usecase.product

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.Outlet
import com.example.kalanacommerce.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetOutletsUseCase(private val repository: ProductRepository) {
    operator fun invoke(): Flow<Resource<List<Outlet>>> {
        return repository.getOutlets()
    }
}