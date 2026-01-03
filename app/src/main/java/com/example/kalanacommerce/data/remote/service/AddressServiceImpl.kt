package com.example.kalanacommerce.data.remote.service

import com.example.kalanacommerce.data.remote.dto.BaseResponse
import com.example.kalanacommerce.data.remote.dto.address.AddressDto
import com.example.kalanacommerce.data.remote.dto.address.AddressRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AddressServiceImpl(
    private val client: HttpClient
) : AddressService {

    override suspend fun getAddresses(): BaseResponse<List<AddressDto>> {
        return client.get("addresses").body()
    }

    override suspend fun getAddressById(id: String): BaseResponse<AddressDto> {
        return client.get("addresses/$id").body()
    }

    override suspend fun createAddress(request: AddressRequest): HttpResponse {
        return client.post("addresses") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun updateAddress(id: String, request: AddressRequest): HttpResponse {
        return client.put("addresses/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun deleteAddress(id: String): HttpResponse {
        return client.delete("addresses/$id")
    }
}