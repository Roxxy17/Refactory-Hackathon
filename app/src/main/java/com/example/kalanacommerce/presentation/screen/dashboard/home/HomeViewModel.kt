package com.example.kalanacommerce.presentation.screen.dashboard.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.Category
import com.example.kalanacommerce.domain.usecase.product.GetCategoriesUseCase
import com.example.kalanacommerce.domain.usecase.product.GetProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            // 1. Load Categories
            getCategoriesUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // Tambahkan opsi "Semua" manual di depan
                        val allCategory = Category(id = "ALL", name = "Semua")
                        val list = listOf(allCategory) + (result.data ?: emptyList())
                        _uiState.update { it.copy(categories = list) }
                        _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
                    }
                    else -> {} // Handle error silent or show toast
                }
            }
        }

        viewModelScope.launch {
            // 2. Load Products (Ambil 10 terbaru)
            getProductsUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        val allData = result.data ?: emptyList()
                        // Ambil 10 produk terbaru (asumsi backend sort by date, kalau tidak, bisa sort manual di sini)
                        val latestProducts = allData.take(10)

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                products = latestProducts,
                                displayProducts = latestProducts
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterData()
    }

    fun onCategorySelected(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
        filterData()
    }

    private fun filterData() {
        val currentState = _uiState.value
        val query = currentState.searchQuery.lowercase()
        val catId = currentState.selectedCategoryId

        val filtered = currentState.products.filter { product ->
            val matchesSearch = product.name.lowercase().contains(query)
            val matchesCategory = if (catId == "ALL") true else product.categoryName == getCategoryNameById(catId) // Note: Idealnya filter by ID di object Product

            matchesSearch // Disederhanakan: Filter search dulu, kategori opsional
        }

        _uiState.update { it.copy(displayProducts = filtered) }
    }

    // Helper sementara karena di Product Domain kita simpan categoryName, bukan ID (sesuai mapper sebelumnya)
    // Nanti bisa disesuaikan mapper-nya kalau mau strict by ID
    private fun getCategoryNameById(id: String): String {
        return _uiState.value.categories.find { it.id == id }?.name ?: ""
    }

    fun refreshData() {
        // 1. Set status refreshing jadi TRUE
        _uiState.update { it.copy(isRefreshing = true) }

        // 2. Panggil ulang data (Gunakan viewModelScope.launch seperti biasa)
        loadHomeData()
    }
}

