package com.example.kalanacommerce.back.service


import com.example.kalanacommerce.back.ChatRequest
import com.example.kalanacommerce.back.ExpressChatResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.statement.bodyAsText

interface ChatService {
    suspend fun sendMessage(prompt: String): Result<String>
}

class ChatServiceImpl(private val client: HttpClient) : ChatService {

    // GANTI IP INI: Gunakan 10.0.2.2 (Emulator) atau IP LAN Anda jika menggunakan perangkat fisik
    private val EXPRESS_CHAT_ENDPOINT = "https://cipta-works.hackathon.sev-2.com/chat"

    override suspend fun sendMessage(prompt: String): Result<String> {
        return try {
            val request = ChatRequest(prompt = prompt)

            val response = client.post(EXPRESS_CHAT_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            // Selalu parse respons sukses HTTP (status 200)
            if (response.status.isSuccess()) {
                val expressResponse: ExpressChatResponse = response.body()

                // Cek status internal dari Express.js
                if (expressResponse.status == "success") {
                    Result.success(expressResponse.response)
                } else {
                    // Server Express mengembalikan status 200, tetapi isinya error
                    Result.failure(Exception(expressResponse.error ?: "Respons Express gagal tanpa pesan error."))
                }
            } else {
                // Server Express mengembalikan status HTTP error (4xx/5xx)
                val errorBody = response.bodyAsText()
                val errorMessage = "Server Express error: ${response.status.value}"

                // Di sini Anda bisa mencoba parse errorBody jika Express mengirim JSON error spesifik
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            // Error koneksi, parsing, atau timeout
            Result.failure(Exception("Koneksi ke Express gagal: ${e.localizedMessage}"))
        }
    }
}