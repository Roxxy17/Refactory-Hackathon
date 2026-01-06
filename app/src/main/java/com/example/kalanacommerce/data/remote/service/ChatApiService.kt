// File: data/remote/service/ChatApiService.kt
package com.example.kalanacommerce.data.remote.service

import com.example.kalanacommerce.data.remote.dto.chat.ChatRequest
import com.example.kalanacommerce.data.remote.dto.chat.ChatResponse

interface ChatApiService {
    suspend fun sendMessage(request: ChatRequest): ChatResponse
}