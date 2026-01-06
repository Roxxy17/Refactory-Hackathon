package com.example.kalanacommerce.domain.repository

import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.remote.dto.chat.ChatResponse
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    // Hanya definisi fungsi
    fun sendMessage(message: String): Flow<Resource<ChatResponse>>
}