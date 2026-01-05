package com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.profilepage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.data.mapper.toDto
import com.example.kalanacommerce.data.remote.dto.user.ProfileUserDto
import com.example.kalanacommerce.domain.model.User
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
        loadUserProfile()
    }

    // --- FUNGSI LOAD DATA (UTAMA) ---
    // isPullRefresh = true jika dipanggil dari tarik-layar (PullToRefresh)
    private fun loadUserProfile(isPullRefresh: Boolean = false) {
        viewModelScope.launch {
            // 1. Load dari Cache (Hanya jika BUKAN refresh tarik, agar data tidak 'lompat')
            // Atau bisa tetap diload untuk memastikan UI terisi dulu
            if (!isPullRefresh) {
                val cachedUserDto = sessionManager.userFlow.firstOrNull()
                if (cachedUserDto != null) {
                    updateLocalUi(cachedUserDto)
                }
            }

            // 2. Ambil data terbaru dari Server
            profileRepository.getProfile().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        // Jika bukan pull refresh dan data kosong, tampilkan loading tengah
                        if (!isPullRefresh && _uiState.value.name.isEmpty()) {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                    is Resource.Success -> {
                        val msg = if (isPullRefresh) "Data profile diperbarui" else null
                        val freshUserDomain: User? = result.data

                        if (freshUserDomain != null) {
                            // KONVERSI DOMAIN -> DTO
                            val userDto = freshUserDomain.toDto()

                            // Update UI Lokal
                            updateLocalUi(userDto)

                            // Simpan ke SessionManager
                            val token = sessionManager.tokenFlow.firstOrNull()
                            if (token != null) {
                                sessionManager.saveSession(token, userDto)
                            }
                        }
                        // Matikan kedua jenis loading
                        _uiState.update { it.copy(isLoading = false, isRefreshing = false, successMessage = msg) }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false, // Pastikan refresh stop saat error
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    // Fungsi refresh yang dipanggil dari UI (PullToRefresh)
    fun refreshData() {
        // Nyalakan indikator refreshing
        _uiState.update { it.copy(isRefreshing = true) }
        // Panggil load data dengan mode refresh
        loadUserProfile(isPullRefresh = true)
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
                        refreshSessionData() // Panggil refresh session internal setelah update
                        _uiState.update { it.copy(isLoading = false, successMessage = "Profil berhasil diperbarui") }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                    }
                }
            }
        }
    }

    fun updatePhoto(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                    ?: throw Exception("Gagal membuka file gambar (Stream Null)")

                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()

                if (originalBitmap != null) {
                    val outputStream = ByteArrayOutputStream()
                    originalBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                    val compressedImageBytes = outputStream.toByteArray()

                    profileRepository.updatePhoto(compressedImageBytes).collect { result ->
                        when (result) {
                            is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                            is Resource.Success -> {
                                refreshSessionData()
                                _uiState.update { it.copy(isLoading = false, successMessage = "Foto berhasil diubah") }
                            }
                            is Resource.Error -> {
                                _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                            }
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Gagal membaca file gambar.")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Gagal memproses gambar: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    private suspend fun refreshSessionData() {
        profileRepository.getProfile().collect { result ->
            if (result is Resource.Success && result.data != null) {
                val userDomain = result.data // Tipe: User

                // Konversi Domain -> DTO
                val userDto = userDomain.toDto()

                val token = sessionManager.tokenFlow.firstOrNull()
                if (token != null) {
                    sessionManager.saveSession(token, userDto)
                }
                updateLocalUi(userDto)
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }
}