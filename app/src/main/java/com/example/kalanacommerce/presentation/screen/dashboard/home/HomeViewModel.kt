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

    // Pastikan HomeUiState sudah punya val successMessage: String? = null
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    // [MODIFIKASI 1] Tambahkan parameter isPullRefresh
    fun loadHomeData(isPullRefresh: Boolean = false) {
        // Set loading state awal
        if (isPullRefresh) {
            _uiState.update { it.copy(isRefreshing = true) }
        } else {
            // Jika bukan refresh (awal buka), set isLoading true
            // (Hanya jika data kosong agar tidak flickering saat refresh diam-diam)
            if (_uiState.value.products.isEmpty()) {
                _uiState.update { it.copy(isLoading = true) }
            }
        }

        // 1. Load Categories (Dijalankan parallel di coroutine sendiri)
        viewModelScope.launch {
            getCategoriesUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val allCategory = Category(id = "ALL", name = "Semua")
                        val list = listOf(allCategory) + (result.data ?: emptyList())
                        _uiState.update { it.copy(categories = list) }
                    }

                    else -> {} // Error categories bisa di-silent atau handle terpisah
                }
            }
        }

        // 2. Load Products (Ini data utama, kita taruh logic finish refresh di sini)
        viewModelScope.launch {
            getProductsUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        val allData = result.data ?: emptyList()

                        // [MODIFIKASI] Acak urutan produk (shuffled) lalu ambil 10
                        // Ini membuat produk yang tampil di Home selalu berubah-ubah
                        val randomProducts = allData.shuffled().take(10)

                        val msg = if (isPullRefresh) "Beranda diperbarui" else null

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                products = randomProducts, // Simpan 10 produk acak ini
                                displayProducts = randomProducts,
                                successMessage = msg
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    // [MODIFIKASI 3] Update fungsi refreshData memanggil loadHomeData dengan parameter true
    fun refreshData() {
        loadHomeData(isPullRefresh = true)
    }

    // [MODIFIKASI 4] Helper untuk membersihkan pesan (dipanggil UI setelah Toast muncul)
    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
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
            // Note: Pastikan getCategoryNameById aman
            val matchesCategory =
                if (catId == "ALL") true else product.categoryName == getCategoryNameById(catId)

            matchesSearch && matchesCategory // Gabungkan kedua filter
        }

        _uiState.update { it.copy(displayProducts = filtered) }
    }

    private fun getCategoryNameById(id: String): String {
        return _uiState.value.categories.find { it.id == id }?.name ?: ""
    }
}