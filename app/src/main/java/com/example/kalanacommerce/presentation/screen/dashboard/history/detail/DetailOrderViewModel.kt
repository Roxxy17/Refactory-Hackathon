package com.example.kalanacommerce.presentation.screen.dashboard.history.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.repository.AddressRepository
import com.example.kalanacommerce.domain.usecase.order.GetOrderDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailOrderViewModel(
    private val getOrderDetailUseCase: GetOrderDetailUseCase,
    private val addressRepository: AddressRepository // [1] Inject Repository Ini
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailOrderUiState())
    val uiState = _uiState.asStateFlow()

    fun loadOrderDetail(orderId: String) {
        viewModelScope.launch {
            // A. Load Order Detail
            getOrderDetailUseCase(orderId).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(isLoading = false, order = result.data)
                        }
                        // [2] Jika Order sukses dimuat, load Alamat Default
                        loadDefaultAddress()
                    }
                    is Resource.Error -> _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    // [3] Fungsi Mencari Alamat Utama
    private fun loadDefaultAddress() {
        viewModelScope.launch {
            addressRepository.getAddresses().collect { result ->
                if (result is Resource.Success) {
                    val addresses = result.data ?: emptyList()
                    // Cari yang isDefault == true
                    val defaultAddress = addresses.find { it.isDefault }

                    if (defaultAddress != null) {
                        _uiState.update {
                            it.copy(
                                defaultLat = defaultAddress.latitude,
                                defaultLong = defaultAddress.longitude
                            )
                        }
                    }
                }
            }
        }
    }
}