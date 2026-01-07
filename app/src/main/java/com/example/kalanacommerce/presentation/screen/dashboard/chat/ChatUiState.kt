package com.example.kalanacommerce.presentation.screen.dashboard.chat

import com.example.kalanacommerce.domain.model.ChatMessage // Pastikan ini satu-satunya referensi

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
// JANGAN ada data class ChatMessage lagi di sini!