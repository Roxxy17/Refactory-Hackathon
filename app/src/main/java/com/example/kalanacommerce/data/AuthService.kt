package com.example.kalanacommerce.data

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

interface AuthService {
    suspend fun signIn(request: SignInRequest): Result<SignInResponse>
    suspend fun register(request: RegisterRequest): Result<RegisterResponse>
}

class AuthServiceImpl(private val client: HttpClient) : AuthService {

    private val BASE_URL = "https://cipta-works.hackathon.sev-2.com"
    private val REGISTER_ENDPOINT = "/auth/register/"
    private val SIGN_IN_ENDPOINT = "/auth/login/"
    private val TAG = "AuthService"

    // Inisialisasi JSON Parser sekali
    private val jsonParser = Json { ignoreUnknownKeys = true; isLenient = true; explicitNulls = false }


    // Helper untuk mengurai respons error
    private suspend fun parseErrorResponse(response: io.ktor.client.statement.HttpResponse): String {
        val errorBodyText = response.bodyAsText()
        Log.e(TAG, "Respons Server Gagal (Status ${response.status.value}): $errorBodyText")

        var errorMessage = "Kesalahan: ${response.status.value}. Silakan coba lagi."

        try {
            // Menggunakan decodeFromString pada String errorBodyText
            val errorResponse: ServerErrorResponse = jsonParser.decodeFromString(errorBodyText)

            // Mengambil pesan error dari properti 'error' atau 'status'
            errorMessage = errorResponse.error ?: errorResponse.status

        } catch (e: SerializationException) {
            Log.w(TAG, "Gagal mengurai body error JSON. ${e.message}")
            // Fallback jika parsing gagal, tetapi kita tahu itu adalah error 4xx/5xx
            errorMessage = "Gagal validasi data. Status: ${response.status.value}"
        }

        return errorMessage
    }

    // --- SIGN IN ---
    override suspend fun signIn(request: SignInRequest): Result<SignInResponse> {
        return try {
            val response = client.post("$BASE_URL$SIGN_IN_ENDPOINT") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status.isSuccess()) {
                val signInResponse: SignInResponse = response.body()
                Result.success(signInResponse)
            } else {
                val errorMessage = parseErrorResponse(response)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Kesalahan jaringan: ${e.localizedMessage}"))
        }
    }

    // --- REGISTER ---
    override suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        return try {
            val response = client.post("$BASE_URL$REGISTER_ENDPOINT") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status.isSuccess()) {
                val registerResponse: RegisterResponse = response.body()
                Result.success(registerResponse)
            } else {
                val errorMessage = parseErrorResponse(response)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Kesalahan jaringan: ${e.localizedMessage}"))
        }
    }
}