package com.example.kalanacommerce.domain.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.remote.dto.address.AddressRequest
import com.example.kalanacommerce.domain.model.Address
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    fun getAddresses(): Flow<Resource<List<Address>>>
    fun getAddressById(id: String): Flow<Resource<Address>> // <--- INI WAJIB ADA
    fun createAddress(request: AddressRequest): Flow<Resource<String>>
    fun updateAddress(id: String, request: AddressRequest): Flow<Resource<String>>
    fun deleteAddress(id: String): Flow<Resource<String>>
}