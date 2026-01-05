package com.example.kalanacommerce.domain.usecase.product

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.Outlet
import com.example.kalanacommerce.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetOutletDetailUseCase(private val repository: ProductRepository) {
    operator fun invoke(id: String): Flow<Resource<Outlet>> {
        return repository.getOutletDetail(id)
    }
}