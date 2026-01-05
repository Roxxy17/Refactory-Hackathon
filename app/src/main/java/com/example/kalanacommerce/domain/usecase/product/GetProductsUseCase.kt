package com.example.kalanacommerce.domain.usecase.product

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.Product
import com.example.kalanacommerce.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetProductsUseCase(private val repository: ProductRepository) {
    operator fun invoke(): Flow<Resource<List<Product>>> {
        return repository.getProducts()
    }
}