package com.example.kalanacommerce.domain.usecase.product

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.Product
import com.example.kalanacommerce.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetProductDetailUseCase(private val repository: ProductRepository) {
    operator fun invoke(id: String): Flow<Resource<Product>> {
        return repository.getProductDetail(id)
    }
}