package com.example.kalanacommerce.back.repository

import com.example.kalanacommerce.back.NewProductRequest
import com.example.kalanacommerce.back.Product
import com.example.kalanacommerce.back.ProductCreationResponse
import com.example.kalanacommerce.back.ProductListResponse
import com.example.kalanacommerce.data.remote.dto.auth.register.RegisterResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.SerializationException
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

    private suspend fun parseErrorResponse(response: HttpResponse): String {
        // 1. Dapatkan kode status HTTP
        val statusCode = response.status.value
        // Inisialisasi pesan error default dengan kode status
        var errorMessage = "Gagal: Status $statusCode"

        // 2. Baca body error. Penting: bodyAsText() hanya bisa dipanggil sekali
        val errorBodyText = try {
            response.bodyAsText()
        } catch (e: Exception) {
            // Jika body tidak bisa dibaca (misal sudah pernah dipanggil atau koneksi putus)
            return "Gagal membaca body error (Status $statusCode)"
        }

        try {
            // Logging: Penting untuk debugging di lingkungan development
            // Log.e("API_ERROR", "Body: $errorBodyText")

            // Coba parsing dengan model RegisterResponse
            val errorResponse: RegisterResponse = jsonParser.decodeFromString(errorBodyText)

            // Asumsi: pesan error selalu ada di properti 'message'
            if (errorResponse.message.isNotEmpty()) {
                errorMessage = errorResponse.message
            } else {
                // Fallback jika 'message' kosong (jarang terjadi)
                errorMessage = "Gagal (Status $statusCode): Pesan dari server kosong."
            }

        } catch (e: SerializationException) {
            // 3. Tampilkan error parsing spesifik
            // Jika struktur JSON errorBodyText tidak sesuai dengan RegisterResponse
            // Log.e("API_PARSE_ERROR", "Gagal parsing JSON Error: ${e.localizedMessage}")
            errorMessage = "Kesalahan format data dari server (Status $statusCode)."

        } catch (e: Exception) {
            // 4. Tangani semua exception lain
            errorMessage = "Kesalahan tak terduga saat memproses respons error: ${e.localizedMessage}"
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