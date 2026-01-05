package com.example.kalanacommerce.domain.usecase.product

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.MeasurementUnit
import com.example.kalanacommerce.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetUnitsUseCase(private val repository: ProductRepository) {
    operator fun invoke(): Flow<Resource<List<MeasurementUnit>>> {
        return repository.getUnits()
    }
}