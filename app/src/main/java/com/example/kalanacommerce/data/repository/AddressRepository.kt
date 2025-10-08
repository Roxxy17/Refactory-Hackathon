// Data/repository/AddressRepository.kt
package com.example.kalanacommerce.data.repository

import com.example.kalanacommerce.data.Address
import com.example.kalanacommerce.data.AddressListResponse
import com.example.kalanacommerce.data.ServerErrorResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json


// com.example.kalanacommerce.data.repository/AddressRepository.kt

// --- Interface ---
interface AddressRepository {
    suspend fun getAddresses(): AddressListResponse
    suspend fun createAddress(address: Address): Address
    // Tambahkan update dan delete jika diperlukan
    suspend fun updateAddress(addressId: Int, address: Address): Address
}

// --- Implementasi ---
class AddressRepositoryImpl(private val httpClient: HttpClient) : AddressRepository {

    // ✅ Menggunakan BASE URL API UTAMA + path router Express
    private val ADDRESS_BASE_URL = "https://cipta-works.hackathon.sev-2.com/auth/addresses"

    // Helper untuk menangani error dari API
    private suspend fun <T> handleResponse(response: io.ktor.client.statement.HttpResponse, successBlock: suspend (io.ktor.client.statement.HttpResponse) -> T): T {
        if (response.status.isSuccess()) {
            return successBlock(response)
        } else {
            val errorBodyText = response.bodyAsText()
            var errorMessage = "Gagal. Status: ${response.status.value}"
            try {
                val errorResponse: ServerErrorResponse = Json { ignoreUnknownKeys = true; isLenient = true }.decodeFromString(errorBodyText)
                errorMessage = errorResponse.error ?: errorResponse.status
            } catch (e: Exception) {
                // Ignore parsing error, use generic message
            }
            throw Exception(errorMessage)
        }
    }

    override suspend fun getAddresses(): AddressListResponse {
        return try {
            val response = httpClient.get(ADDRESS_BASE_URL)
            handleResponse(response) { it.body<AddressListResponse>() }
        } catch (e: Exception) {
            throw Exception("Gagal memuat alamat: ${e.message}")
        }
    }

    override suspend fun createAddress(address: Address): Address {
        return try {
            val response = httpClient.post(ADDRESS_BASE_URL) {
                contentType(ContentType.Application.Json)
                setBody(address)
            }
            // Asumsi: Respons 201 mengembalikan object { status: "Sukses", address: {...} }
            // Anda mungkin perlu menyesuaikan pemanggilan body() tergantung struktur respons success API Anda.
            // Jika API mengembalikan Address langsung, gunakan: handleResponse(response) { it.body<Address>() }
            handleResponse(response) {
                it.body<Address>() // Mengambil Address dari body
            }
        } catch (e: Exception) {
            throw Exception("Gagal membuat alamat: ${e.message}")
        }
    }

    override suspend fun updateAddress(addressId: Int, address: Address): Address {
        return try {
            val response = httpClient.put("$ADDRESS_BASE_URL/$addressId") {
                contentType(ContentType.Application.Json)
                setBody(address)
            }
            handleResponse(response) {
                it.body<Address>() // Mengambil Address dari body
            }
        } catch (e: Exception) {
            throw Exception("Gagal memperbarui alamat: ${e.message}")
        }
    }
}