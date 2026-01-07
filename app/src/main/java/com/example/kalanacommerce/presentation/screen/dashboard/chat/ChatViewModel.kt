package com.example.kalanacommerce.presentation.screen.dashboard.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.ChatMessage
import com.example.kalanacommerce.domain.usecase.chat.SendMessageUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
    welcomeMessage: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(messages = listOf(ChatMessage(welcomeMessage, false)))
        }
    }

    fun sendMessage(message: String) {
        val userMsg = ChatMessage(message, true)
        _uiState.update { it.copy(messages = it.messages + userMsg, isLoading = true) }

        viewModelScope.launch {
            sendMessageUseCase(message).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                messages = it.messages + result.data!!,
                                isLoading = false,
                                error = null // Reset error
                            )
                        }
                    }
                    is Resource.Error -> {
                        // [PERBAIKAN] Tampilkan pesan error sebagai chat bubble agar terlihat user
                        val errorMsg = ChatMessage(
                            text = "Maaf, terjadi kesalahan: ${result.message ?: "Unknown error"}",
                            isUser = false
                        )
                        _uiState.update {
                            it.copy(
                                messages = it.messages + errorMsg,
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                    is Resource.Loading -> {
                        // Opsional: Handle loading state jika perlu update UI spesifik
                    }
                }
            }
        }
    }
}