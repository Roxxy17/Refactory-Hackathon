package com.example.kalanacommerce.presentation.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.back.service.ChatService
import com.example.kalanacommerce.presentation.screen.dashboard.Message

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- Data State untuk UI ---
data class ChatUiState(
    val messages: List<Message> = listOf(
        // Pesan sambutan awal dari AI (Kak Lana)
        Message("Halo kak, Kak Lana di sini. Ada yang bisa saya bantu hari ini?", isUser = false)
    ),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatViewModel(private val chatService: ChatService) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState
    init {
        // Panggil fungsi inisialisasi chat
        startChat()
    }

    /**
     * Mengirim instruksi awal ke AI untuk menentukan peran atau untuk inisialisasi.
     * Catatan: Karena menggunakan Express, ini akan menjadi pesan pertama di riwayat.
     */
    private fun startChat() {
        val initialInstruction = "Jadi anda adalah seorang cs. Peran anda adalah asisten yang ramah dan sopan bernama Kak Lana. Jawab dengan nada sopan dan profesional., anda akan menjawb terkati KalanaCommerce, sebuah ecommerce pangan yang mewadahi para petani dan usaha lokal"

        // Kita kirimkan ini sebagai prompt pertama dari user (tapi jangan tampilkan di UI!)
        // Kita perlu versi sendMessage yang tidak menambah ke UI
        sendHiddenPrompt(initialInstruction)
    }

    /**
     * Fungsi baru untuk mengirim prompt ke Service TANPA memperbarui UI.
     * Ini memerlukan modifikasi pada logika Service dan ViewModel.
     */
    private fun sendHiddenPrompt(prompt: String) {
        if (prompt.isBlank()) return

        // Atur loading state, meskipun pesannya tersembunyi
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            // Kita asumsikan chatService.sendMessage akan menggunakan prompt ini untuk inisialisasi
            val result = chatService.sendMessage(prompt)

            result.onSuccess { responseText ->
                // Jika sukses, kita hanya menambahkan balasan AI pertama ke UI
                val aiMessage = Message(text = responseText, isUser = false)
                _uiState.update {
                    it.copy(
                        messages = listOf(aiMessage), // HANYA balasan AI yang pertama yang muncul di UI
                        isLoading = false
                    )
                }
            }.onFailure { exception ->
                // Tangani error jaringan saat startup
                Log.e("ChatViewModel", "Gagal mengirim prompt awal: ${exception.message}")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    /**
     * Mengirim prompt pengguna ke Chat Service (Express.js) dan menunggu respons AI.
     */
    fun sendMessage(prompt: String) {
        if (prompt.isBlank() || _uiState.value.isLoading) return

        // 1. Tambahkan pesan pengguna ke UI dan set loading state
        val userMessage = Message(text = prompt, isUser = true)
        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            val result = chatService.sendMessage(prompt)

            result.onSuccess { responseText ->
                // 2. Tambahkan respons AI ke UI
                val aiMessage = Message(text = responseText, isUser = false)
                _uiState.update {
                    it.copy(
                        messages = it.messages + aiMessage,
                        isLoading = false
                    )
                }
            }.onFailure { exception ->
                // 3. Tangani kegagalan (jaringan, Express.js, atau Gemini)
                val errorMessage = exception.message ?: "Kesalahan yang tidak diketahui."

                // Tambahkan pesan error sebagai balasan AI
                val errorMessageForUser = Message(text = "❌ Error: $errorMessage", isUser = false)

                _uiState.update {
                    it.copy(
                        messages = it.messages + errorMessageForUser,
                        isLoading = false,
                        error = errorMessage
                    )
                }
            }
        }
    }
}