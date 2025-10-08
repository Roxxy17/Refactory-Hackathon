package com.example.kalanacommerce.data.repository

// com.example.kalanacommerce.data.repository/ProductRepository.kt

// ... (Import yang diperlukan)
// com.example.kalanacommerce.data.repository/ProductRepository.kt

import com.example.kalanacommerce.data.NewProductRequest
import com.example.kalanacommerce.data.Product
import com.example.kalanacommerce.data.ProductCreationResponse
import com.example.kalanacommerce.data.ProductListResponse
import com.example.kalanacommerce.data.ServerErrorResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.json.Json


interface ProductRepository {
    // GET: Mengambil daftar produk publik (Katalog)
    suspend fun getProducts(): ProductListResponse
    // POST: Membuat produk baru (Memerlukan otorisasi Seller)
    suspend fun createProduct(request: NewProductRequest): Result<Product>
    // ... (Fungsi PUT/DELETE lainnya, jika diperlukan)
}

class ProductRepositoryImpl(private val client: HttpClient) : ProductRepository {

    // ✅ ENDPOINT GET PUBLIK (Sesuai URL yang Anda berikan)
    private val GET_PRODUCTS_URL = "https://cipta-works.hackathon.sev-2.com/catalog/products/"

    // ✅ ENDPOINT POST/PUT TERPROTEKSI (Sesuai router Express sebelumnya)
    private val AUTH_PRODUCTS_URL = "https://cipta-works.hackathon.sev-2.com/api/v1/products"

    // Inisialisasi JSON Parser untuk error
    private val jsonParser = Json { ignoreUnknownKeys = true; isLenient = true; explicitNulls = false }

    // Helper untuk mengurai respons error (asumsi ini sudah Anda definisikan)
    private suspend fun parseErrorResponse(response: io.ktor.client.statement.HttpResponse): String {
        val errorBodyText = response.bodyAsText()
        var errorMessage = "Gagal: Status ${response.status.value}"
        try {
            val errorResponse: ServerErrorResponse = jsonParser.decodeFromString(errorBodyText)
            errorMessage = errorResponse.error ?: errorResponse.status
        } catch (e: Exception) {
            // Biarkan pesan default jika parsing gagal
        }
        return errorMessage
    }

    // --- 1. GET: MENGAMBIL DAFTAR PRODUK (Katalog) ---
    override suspend fun getProducts(): ProductListResponse {
        return try {
            val response = client.get(GET_PRODUCTS_URL)

            if (response.status.isSuccess()) {
                response.body<ProductListResponse>()
            } else {
                val errorMessage = parseErrorResponse(response)
                throw Exception(errorMessage)
            }
        } catch (e: Exception) {
            throw Exception("Kesalahan jaringan saat memuat katalog: ${e.localizedMessage}")
        }
    }

    // --- 2. POST: MEMBUAT PRODUK BARU (Seller) ---
    override suspend fun createProduct(request: NewProductRequest): Result<Product> {
        return try {
            // Mengirim data ke endpoint terproteksi
            val response = client.post(AUTH_PRODUCTS_URL) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status.isSuccess()) {
                // Asumsi: Respons 201 mengembalikan object { status: "Sukses", product: {...} }
                val creationResponse: ProductCreationResponse = response.body()
                Result.success(creationResponse.product)
            } else {
                val errorMessage = parseErrorResponse(response)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Kesalahan jaringan saat membuat produk: ${e.localizedMessage}"))
        }
    }
}