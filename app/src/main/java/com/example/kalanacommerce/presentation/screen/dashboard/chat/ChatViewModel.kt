package com.example.kalanacommerce.presentation.screen.dashboard.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.remote.dto.chat.ChatProductRecommendation
import com.example.kalanacommerce.domain.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

// Model UI untuk Pesan
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val recommendations: List<ChatProductRecommendation> = emptyList() // List produk
)

data class ChatUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val messages: List<ChatMessage> = emptyList()
)

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    // JSON Parser yang toleran (ignore unknown keys)
    private val jsonParser = Json { ignoreUnknownKeys = true; isLenient = true }

    fun sendMessage(text: String) {
        // 1. Tambahkan pesan user ke UI langsung (Optimistic Update)
        val userMsg = ChatMessage(text = text, isUser = true)
        addMessage(userMsg)

        viewModelScope.launch {
            repository.sendMessage(text).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        val rawResponse = result.data?.response ?: ""

                        // 2. Parsing AI Response
                        val parsedMessage = parseAiResponse(rawResponse)
                        addMessage(parsedMessage)
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                        // Opsional: Tambahkan pesan error sebagai chat bubble
                        addMessage(ChatMessage("Maaf, saya sedang bermasalah: ${result.message}", isUser = false))
                    }
                }
            }
        }
    }

    private fun addMessage(msg: ChatMessage) {
        _uiState.update { current ->
            current.copy(messages = current.messages + msg)
        }
    }

    // --- LOGIKA PARSING <checkout> ---
    private fun parseAiResponse(raw: String): ChatMessage {
        // Regex untuk mencari string di antara <checkout> dan </checkout>
        // (?s) agar dot (.) cocok dengan newline
        val regex = "(?s)<checkout>(.*?)</checkout>".toRegex()
        val matchResult = regex.find(raw)

        var cleanText = raw
        var products: List<ChatProductRecommendation> = emptyList()

        if (matchResult != null) {
            // 1. Ambil JSON String di dalam tag
            val jsonString = matchResult.groupValues[1].trim()

            // 2. Hapus tag XML dari teks utama agar bersih saat ditampilkan
            cleanText = raw.replace(regex, "").trim()

            // 3. Parsing JSON String ke List Object
            if (jsonString.isNotEmpty() && jsonString != "[]") {
                try {
                    products = jsonParser.decodeFromString<List<ChatProductRecommendation>>(jsonString)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Jika gagal parse, biarkan list kosong tapi teks tetap muncul
                }
            }
        }

        return ChatMessage(
            text = cleanText,
            isUser = false,
            recommendations = products
        )
    }
}