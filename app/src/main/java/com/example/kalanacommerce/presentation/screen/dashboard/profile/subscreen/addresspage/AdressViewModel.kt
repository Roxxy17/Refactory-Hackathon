package com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.addresspage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.remote.dto.address.AddressRequest
import com.example.kalanacommerce.domain.repository.AddressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// PENTING: Pastikan AddressUiState punya properti: val isRefreshing: Boolean = false
class AddressViewModel(
    private val repository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddressUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAddresses()
    }

    // --- FUNGSI REFRESH ---
    fun refreshAddresses() {
        // Panggil loadAddresses dengan mode refresh
        loadAddresses(isPullRefresh = true)
    }

    // Modified loadAddresses
    fun loadAddresses(isPullRefresh: Boolean = false) {
        viewModelScope.launch {
            repository.getAddresses().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        if (isPullRefresh) {
                            _uiState.update { it.copy(isRefreshing = true) }
                        } else {
                            _uiState.update { it.copy(isLoading = true, error = null) }
                        }
                    }

                    is Resource.Success -> {

                        val msg = if (isPullRefresh) "Data alamat diperbarui" else null
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false, // Matikan refresh
                                addresses = result.data ?: emptyList(),
                                successMessage = msg
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false, // Matikan refresh
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    // ... (Fungsi loadAddressDetail, saveAddress, deleteAddress, clearMessages TETAP SAMA) ...
    // ... Copy paste sisa fungsi dari file lamamu di sini ...

    fun loadAddressDetail(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedAddress = null) }
            repository.getAddressById(id).collect { result ->
                handleResult(result) { data ->
                    _uiState.update { it.copy(selectedAddress = data) }
                }
            }
        }
    }

    fun saveAddress(
        isEdit: Boolean,
        addressId: String? = null,
        label: String,
        name: String,
        phone: String,
        street: String,
        postalCode: String,
        provinceId: String,
        cityId: String,
        districtId: String,
        isDefault: Boolean,
        latitude: Double,
        longitude: Double
    ) {
        viewModelScope.launch {
            val request = AddressRequest(
                label,
                name,
                phone,
                street,
                postalCode,
                provinceId,
                cityId,
                districtId,
                isDefault,
                lat = latitude, // Kirim ke API
                long = longitude // Kirim ke API
            )
            val flow = if (isEdit && addressId != null) repository.updateAddress(
                addressId,
                request
            ) else repository.createAddress(request)

            flow.collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false, successMessage = result.data) }
                        loadAddresses()
                    }

                    is Resource.Error -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun deleteAddress(id: String) {
        viewModelScope.launch {
            repository.deleteAddress(id).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false, successMessage = result.data) }
                        loadAddresses()
                    }

                    is Resource.Error -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null, selectedAddress = null) }
    }

    private fun <T> handleResult(result: Resource<T>, onSuccess: (T?) -> Unit) {
        when (result) {
            is Resource.Loading -> _uiState.update { it.copy(isLoading = true, error = null) }
            is Resource.Success -> {
                _uiState.update { it.copy(isLoading = false) }
                onSuccess(result.data)
            }

            is Resource.Error -> {
                _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }


}