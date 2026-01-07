package com.example.kalanacommerce.domain.model

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val recommendations: List<Product> = emptyList() // Menggunakan Domain Product
)