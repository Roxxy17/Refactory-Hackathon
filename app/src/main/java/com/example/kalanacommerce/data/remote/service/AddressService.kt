package com.example.kalanacommerce.data.remote.service

import com.example.kalanacommerce.data.remote.dto.BaseResponse
import com.example.kalanacommerce.data.remote.dto.address.AddressDto
import com.example.kalanacommerce.data.remote.dto.address.AddressRequest
import io.ktor.client.statement.HttpResponse

interface AddressService {
    suspend fun getAddresses(): BaseResponse<List<AddressDto>>
    suspend fun getAddressById(id: String): BaseResponse<AddressDto> // <--- INI WAJIB ADA
    suspend fun createAddress(request: AddressRequest): HttpResponse
    suspend fun updateAddress(id: String, request: AddressRequest): HttpResponse
    suspend fun deleteAddress(id: String): HttpResponse
}