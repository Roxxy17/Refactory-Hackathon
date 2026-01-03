package com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.addresspage


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.data.remote.dto.address.AddressRequest
import com.example.kalanacommerce.domain.model.Address
import com.example.kalanacommerce.domain.repository.AddressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// DEFINISI UiState (HANYA BOLEH ADA DI SINI)

class AddressViewModel(
    private val repository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddressUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAddresses()
    }

    fun loadAddresses() {
        viewModelScope.launch {
            repository.getAddresses().collect { result ->
                handleResult(result) { data ->
                    _uiState.update { it.copy(addresses = data ?: emptyList()) }
                }
            }
        }
    }

    fun loadAddressDetail(id: String) {
        viewModelScope.launch {
            // Reset dulu
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
        isDefault: Boolean
    ) {
        viewModelScope.launch {
            val request = AddressRequest(
                label = label,
                recipientName = name,
                phoneNumber = phone,
                street = street,
                postalCode = postalCode,
                provincesId = provinceId,
                citiesId = cityId,
                districtsId = "ID-JK-JKT-GM",
                isDefault = isDefault
            )

            val flow = if (isEdit && addressId != null) {
                repository.updateAddress(addressId, request)
            } else {
                repository.createAddress(request)
            }

            flow.collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(isLoading = false, successMessage = result.data)
                        }
                        loadAddresses()
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }

    fun deleteAddress(id: String) {
        viewModelScope.launch {
            repository.deleteAddress(id).collect { result ->
                when(result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false, successMessage = result.data) }
                        loadAddresses()
                    }
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
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