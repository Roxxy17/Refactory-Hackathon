// File: data/remote/service/ChatApiServiceImpl.kt
package com.example.kalanacommerce.data.remote.service

import com.example.kalanacommerce.data.remote.dto.chat.ChatRequest
import com.example.kalanacommerce.data.remote.dto.chat.ChatResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ChatApiServiceImpl(
    private val client: HttpClient
) : ChatApiService {

    override suspend fun sendMessage(request: ChatRequest): ChatResponse {
        // "chatbot" akan otomatis digabung dengan BASE_URL yang ada di NetworkModule
        return client.post("chatbot") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}