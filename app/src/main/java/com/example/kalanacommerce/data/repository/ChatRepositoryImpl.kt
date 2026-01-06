package com.example.kalanacommerce.data.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.remote.dto.chat.ChatRequest
import com.example.kalanacommerce.data.remote.dto.chat.ChatResponse
import com.example.kalanacommerce.data.remote.service.ChatApiService
import com.example.kalanacommerce.domain.repository.ChatRepository // Import Interface Domain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChatRepositoryImpl(
    private val api: ChatApiService
) : ChatRepository { // Implementasi Interface Domain

    override fun sendMessage(message: String): Flow<Resource<ChatResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.sendMessage(ChatRequest(message))
            if (response.status) {
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error("Terjadi kesalahan pada AI."))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Gagal terhubung ke server"))
        }
    }
}