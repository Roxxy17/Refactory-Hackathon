package com.example.kalanacommerce.presentation.screen.dashboard.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.Category
import com.example.kalanacommerce.domain.model.Product
import com.example.kalanacommerce.domain.repository.ProductRepository // Pastikan ini ada
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExploreUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val categories: List<Category> = emptyList(), // Untuk tampilan awal
    val searchResults: List<Product> = emptyList() // Untuk hasil pencarian
)

class ExploreViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        fetchCategories()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        // Debounce: Tunggu user selesai mengetik 500ms baru cari ke API
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isNotEmpty()) {
                delay(500)
                searchProducts(query)
            } else {
                _uiState.update { it.copy(searchResults = emptyList()) }
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            // Asumsi: repository.getCategories() memanggil GET /categories
            repository.getCategories().collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(isLoading = false, categories = result.data ?: emptyList())
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }

    private fun searchProducts(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Asumsi: repository.getProducts(query) memanggil GET /products?search=...
            repository.getProducts(query).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(isLoading = false, searchResults = result.data ?: emptyList())
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, error = result.message)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}