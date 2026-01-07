package com.example.kalanacommerce.data.mapper

import com.example.kalanacommerce.data.remote.dto.chat.ChatResponse
import com.example.kalanacommerce.domain.model.ChatMessage
import com.example.kalanacommerce.domain.model.Product

// data/mapper/ChatMapper.kt
fun ChatResponse.toDomain(recommendations: List<Product>): ChatMessage {
    val checkoutRegex = "<checkout>(.*?)</checkout>".toRegex(RegexOption.DOT_MATCHES_ALL)
    val cleanText = this.response.replace(checkoutRegex, "").trim()

    return ChatMessage(
        text = cleanText,
        isUser = false,
        recommendations = recommendations
    )
}