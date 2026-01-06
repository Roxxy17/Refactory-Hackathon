package com.example.kalanacommerce.data.remote.dto.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val message: String
)

@Serializable
data class ChatResponse(
    val status: Boolean,
    val response: String // String mentah berisi teks + <checkout>JSON</checkout>
)

// Model untuk produk yang ada DI DALAM tag <checkout>
// Asumsi: Backend akan mengirim detail lengkap (nama, harga, gambar)
// Jika backend hanya kirim ID, kamu perlu API call tambahan (getProductById).
@Serializable
data class ChatProductRecommendation(
    val id: String,
    val name: String? = "Produk Rekomendasi", // Fallback jika backend cuma kirim ID
    val price: Long? = 0,
    val image: String? = "",
    val qty: Int? = 1
)