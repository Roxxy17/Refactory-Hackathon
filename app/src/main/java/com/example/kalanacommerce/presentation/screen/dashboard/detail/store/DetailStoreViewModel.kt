package com.example.kalanacommerce.presentation.screen.dashboard.detail.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailStoreViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailStoreUiState())
    val uiState = _uiState.asStateFlow()

    // Load data toko dan produknya
    fun loadStoreData(outletId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. Ambil Detail Outlet
            repository.getOutletDetail(outletId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(outlet = result.data) }
                        // Jika sukses ambil detail toko, lanjut ambil produk
                        fetchProductsForOutlet(outletId)
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    private fun fetchProductsForOutlet(outletId: String) {
        viewModelScope.launch {
            // Ambil semua produk (API biasanya support filter by outlet_id,
            // tapi disini kita filter manual dari list products jika API belum support)
            repository.getProducts("").collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val allProducts = result.data ?: emptyList()
                        // Filter produk yang outletName-nya sama atau ID-nya cocok
                        // (Asumsi di domain model Product ada field outletName atau kita perlu mapping ID)
                        // Karena di UI Detail Product kita sudah mapping outlet, kita pakai logic yang sama.

                        // CATATAN: Idealnya Product punya field `outletId`.
                        // Jika belum ada di domain Product, kita filter sementara based on Logic/Name
                        // atau asumsikan backend mengirim data yang benar.
                        // Di sini saya filter manual:
                        val storeProducts = allProducts.filter {
                            it.outlet?.id == outletId || it.outletName == _uiState.value.outlet?.name
                        }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                outletProducts = storeProducts
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun onCategoryFilterClicked(category: String) {
        _uiState.update { it.copy(selectedCategoryFilter = category) }
    }
}