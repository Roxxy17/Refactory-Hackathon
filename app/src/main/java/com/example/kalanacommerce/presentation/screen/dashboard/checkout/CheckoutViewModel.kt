package com.example.kalanacommerce.presentation.screen.dashboard.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.usecase.cart.CheckoutUseCase
import com.example.kalanacommerce.domain.usecase.cart.GetCartItemsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val checkoutUseCase: CheckoutUseCase
    // private val getAddressUseCase: GetAddressUseCase // (Inject jika sudah ada)
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState = _uiState.asStateFlow()

    // 1. Load Data Barang yang Mau Dibeli
    fun loadCheckoutItems(itemIds: List<String>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Ambil semua cart items, lalu filter yang ID-nya cocok
            getCartItemsUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val allItems = result.data ?: emptyList()
                        val selectedItems = allItems.filter { it.id in itemIds }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                checkoutItems = selectedItems
                            )
                        }

                        // Load alamat setelah load item (simulasi)
                        loadDefaultAddress()
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    // 2. Load Alamat (Simulasi / TODO: Ganti dengan UseCase Address asli)
    private fun loadDefaultAddress() {
        // Simulasi data alamat
        val dummyAddress = AddressUiModel(
            id = "addr_001",
            name = "Kalila Atha",
            phone = "081234567890",
            address = "Jl. Mawar Melati No. 123, Sleman, Yogyakarta",
            isMain = true
        )
        _uiState.update { it.copy(selectedAddress = dummyAddress) }
    }

    // 3. Proses Bayar (Panggil API)
    fun placeOrder() {
        val currentItems = _uiState.value.checkoutItems
        if (currentItems.isEmpty()) return

        val itemIds = currentItems.map { it.id }

        viewModelScope.launch {
            checkoutUseCase(itemIds).collect { result ->
                when(result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        // Ambil hasil pertama
                        val data = result.data?.firstOrNull()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                checkoutResult = data
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }
}