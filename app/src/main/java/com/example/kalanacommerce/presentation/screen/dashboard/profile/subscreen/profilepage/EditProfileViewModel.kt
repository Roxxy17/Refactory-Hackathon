package com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.profilepage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.data.remote.dto.user.ProfileUserDto
import com.example.kalanacommerce.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class EditProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchProfileData()
    }

    private fun fetchProfileData() {
        viewModelScope.launch {
            // 1. Load dari Cache (SessionManager) dulu
            val cachedUser = sessionManager.userFlow.firstOrNull()
            if (cachedUser != null) {
                updateLocalUi(cachedUser)
            }

            // 2. Ambil data terbaru dari Server
            profileRepository.getProfile().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        if (cachedUser == null) {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                    is Resource.Success -> {
                        val freshUser = result.data
                        if (freshUser != null) {
                            updateLocalUi(freshUser)
                            // Simpan ke SessionManager agar sinkron
                            val token = sessionManager.tokenFlow.firstOrNull()
                            if (token != null) {
                                sessionManager.saveSession(token, freshUser)
                            }
                        }
                        _uiState.update { it.copy(isLoading = false) }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message // Pesan detail dari Repository
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updateLocalUi(user: ProfileUserDto) {
        _uiState.update {
            it.copy(
                name = user.name ?: "",
                email = user.email ?: "",
                phoneNumber = user.phoneNumber ?: "",
                balance = user.balance ?: "0",
                profileImage = user.image
            )
        }
    }

    fun onNameChange(newValue: String) { _uiState.update { it.copy(name = newValue) } }
    fun onPhoneChange(newValue: String) { _uiState.update { it.copy(phoneNumber = newValue) } }

    fun updateProfile() {
        viewModelScope.launch {
            val currentState = _uiState.value

            profileRepository.updateProfile(
                name = currentState.name,
                email = currentState.email,
                phone = currentState.phoneNumber
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        refreshSessionData()
                        _uiState.update { it.copy(isLoading = false, successMessage = "Profil berhasil diperbarui") }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                    }
                }
            }
        }
    }

    // --- FUNGSI UPDATE PHOTO DENGAN TRY-CATCH YANG KUAT ---
    fun updatePhoto(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) } // Mulai Loading

                // 1. Buka Stream dari URI
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                    ?: throw Exception("Gagal membuka file gambar (Stream Null)")

                // 2. Decode ke Bitmap
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close() // Selalu tutup stream setelah dipakai

                if (originalBitmap != null) {
                    // 3. KOMPRESI GAMBAR (Cegah Error 413 Entity Too Large)
                    val outputStream = ByteArrayOutputStream()

                    // Kompres ke JPEG dengan kualitas 50%
                    originalBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

                    val compressedImageBytes = outputStream.toByteArray()

                    // 4. Kirim ke Repository
                    profileRepository.updatePhoto(compressedImageBytes).collect { result ->
                        when (result) {
                            is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                            is Resource.Success -> {
                                refreshSessionData()
                                _uiState.update { it.copy(isLoading = false, successMessage = "Foto berhasil diubah") }
                            }
                            is Resource.Error -> {
                                // Error dari Server/Jaringan (sudah didetailkan di Repository)
                                _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                            }
                        }
                    }
                } else {
                    // Error jika gambar tidak bisa dibaca (misal format tidak didukung)
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Gagal membaca file gambar. Format mungkin tidak didukung.")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Error LOKAL (Processing Error)
                // Ini menangkap error seperti OutOfMemory, File tidak ditemukan, dll.
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memproses gambar di HP: ${e.localizedMessage ?: "Error tidak diketahui"}"
                    )
                }
            }
        }
    }

    private suspend fun refreshSessionData() {
        profileRepository.getProfile().collect { result ->
            if (result is Resource.Success && result.data != null) {
                val newUser = result.data
                val token = sessionManager.tokenFlow.firstOrNull()

                if (token != null) {
                    sessionManager.saveSession(token, newUser)
                }
                updateLocalUi(newUser)
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }
}