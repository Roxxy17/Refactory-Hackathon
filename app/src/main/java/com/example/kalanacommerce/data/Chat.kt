package com.example.kalanacommerce.data

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable


@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ChatRequest(
    val prompt: String
)

// Respons yang diharapkan dari Express.js
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ExpressChatResponse(
    val status: String,
    val response: String, // Ini adalah pesan balasan dari Gemini AI
    val error: String? = null // Untuk menangani error dari Express
)