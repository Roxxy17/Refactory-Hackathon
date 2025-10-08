package com.example.kalanacommerce.ui.viewmodel

// Presentation/viewmodel/AddressViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.data.Address
import com.example.kalanacommerce.data.repository.AddressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AddressUiState(
    val addresses: List<Address> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class AddressViewModel(private val repository: AddressRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AddressUiState())
    val uiState: StateFlow<AddressUiState> = _uiState

    init {
        loadAddresses()
    }

    fun loadAddresses() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val response = repository.getAddresses()
                if (response.status == "Sukses") {
                    _uiState.value = _uiState.value.copy(
                        addresses = response.addresses,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Gagal memuat alamat: ${response.status}",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Kesalahan jaringan: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    // Fungsi untuk membuat alamat baru
    fun createAddress(addressLine: String, city: String, postalCode: String, isPrimary: Boolean) {
        viewModelScope.launch {
            // Kita harus kirim city dan postal_code ke backend,
            // meskipun di UI Compose kita hanya ambil addressLine.
            val newAddress = Address(
                address_line = addressLine,
                city = city, // Nilai dummy atau default dari konfigurasi
                postal_code = postalCode, // Nilai dummy atau default dari konfigurasi
                is_primary = isPrimary
            )
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                val created = repository.createAddress(newAddress)
                // Setelah berhasil, muat ulang daftar alamat
                loadAddresses()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Gagal membuat alamat: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
}