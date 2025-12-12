package com.example.kalanacommerce.data.repository

// com.example.kalanacommerce.data.repository/OrderRepository.kt

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import com.example.kalanacommerce.data.model.* // Import semua model pesanan
import kotlinx.serialization.json.Json

interface OrderRepository {
    // POST: Checkout (Membuat pesanan baru)
    suspend fun createOrder(request: NewOrderRequest): Result<OrderResponse>
    // GET: Mengambil daftar pesanan pengguna
    suspend fun getMyOrders(): OrderListResponse
}

class OrderRepositoryImpl(private val client: HttpClient) : OrderRepository {

    private val ORDERS_URL = "https://cipta-works.hackathon.sev-2.com/orders/orders"

    private suspend fun parseErrorResponse(response: io.ktor.client.statement.HttpResponse): String {
        val errorBodyText = response.bodyAsText()
        val jsonParser = Json { ignoreUnknownKeys = true; isLenient = true }
        var errorMessage = "Gagal: Status ${response.status.value}"
        try {
            val errorResponse: ServerErrorResponse = jsonParser.decodeFromString(errorBodyText)
            errorMessage = errorResponse.error ?: errorResponse.status
        } catch (e: Exception) { /* ignore */ }
        return errorMessage
    }

    // --- 1. POST: MEMBUAT PESANAN BARU (CHECKOUT) ---
    override suspend fun createOrder(request: NewOrderRequest): Result<OrderResponse> {
        return try {
            val response = client.post(ORDERS_URL) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status.isSuccess()) {
                val creationResponse: OrderCreationResponse = response.body()
                Result.success(creationResponse.order)
            } else {
                val errorMessage = parseErrorResponse(response)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Kesalahan jaringan saat checkout: ${e.localizedMessage}"))
        }
    }

    // --- 2. GET: DAFTAR PESANAN SAYA ---
    override suspend fun getMyOrders(): OrderListResponse {
        return try {
            val response = client.get(ORDERS_URL)

            if (response.status.isSuccess()) {
                response.body<OrderListResponse>()
            } else {
                val errorMessage = parseErrorResponse(response)
                throw Exception(errorMessage)
            }
        } catch (e: Exception) {
            throw Exception("Gagal memuat daftar pesanan: ${e.localizedMessage}")
        }
    }
}