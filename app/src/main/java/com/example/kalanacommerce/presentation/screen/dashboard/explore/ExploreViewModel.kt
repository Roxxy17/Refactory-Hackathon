package com.example.kalanacommerce.presentation.screen.dashboard.explore

import ExploreUiState
import UiCategory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.Category
import com.example.kalanacommerce.domain.repository.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.kalanacommerce.R // Pastikan import ini ada

class ExploreViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    // Daftar Kategori Statis (Sesuai UI dan strings.xml)
    private val staticUiCategories = listOf(
        UiCategory("CAT_PAKET", R.string.cat_exp_packet, "Paket Masak", listOf("paket", "kit")),
        UiCategory("CAT_SAYUR", R.string.cat_exp_vegetable, "Sayuran", listOf("sayur", "vegetable", "hijau", "daun")),
        UiCategory("CAT_BUAH", R.string.cat_exp_fruit, "Buah", listOf("buah", "fruit")),
        UiCategory("CAT_DAGING", R.string.cat_exp_meat, "Daging Ikan Ayam", listOf("daging", "meat", "ayam", "chicken", "ikan", "fish", "sapi", "beef", "seafood", "udang")),
        UiCategory("CAT_NABATI", R.string.cat_plant_protein, "Tahu Tempe", listOf("tahu", "tofu", "tempe", "tempeh", "nabati")),
        UiCategory("CAT_POKOK", R.string.cat_staple, "Beras Minyak Telur", listOf("beras", "rice", "minyak", "oil", "telur", "egg", "gula", "sugar", "pokok", "staple", "sembako")),
        UiCategory("CAT_BUMBU", R.string.cat_exp_spice, "Bumbu", listOf("bumbu", "spice", "rempah", "kunyit", "jahe", "bawang")),
        UiCategory("CAT_OLAHAN", R.string.cat_processed, "Sosis Nugget Bakso", listOf("sosis", "sausage", "nugget", "bakso", "meatball", "olahan", "processed")),
        UiCategory("CAT_INSTAN", R.string.cat_instant, "Mie Instan", listOf("mie", "noodle", "instan", "instant", "pasta"))
    )

    init {
        // Mapping UiCategory ke Domain Category
        // name diisi ID ("CAT_...") agar nanti UI bisa membedakannya dan mengambil string resource yang tepat
        val mappedCategories = staticUiCategories.map {
            Category(id = it.id, name = it.id)
        }
        _uiState.update { it.copy(categories = mappedCategories) }
    }

    // --- Search Logic ---
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query, selectedCategory = null) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isNotEmpty()) {
                delay(500)
                // Pencarian manual: validationKeywords = null
                searchProducts(query, validationKeywords = null)
            } else {
                _uiState.update { it.copy(searchResults = emptyList()) }
            }
        }
    }

    // --- Category Logic ---
    fun onCategorySelected(category: Category) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                searchQuery = ""
            )
        }

        // Cari config berdasarkan ID
        val config = staticUiCategories.find { it.id == category.id }

        if (config != null) {
            searchProducts(config.apiQuery, config.validationKeywords)
        }
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "", searchResults = emptyList()) }
    }

    fun clearCategory() {
        _uiState.update { it.copy(selectedCategory = null, searchResults = emptyList()) }
    }

    // --- Core Search Function ---
    private fun searchProducts(query: String, validationKeywords: List<String>? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getProducts(query).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val rawData = result.data ?: emptyList()

                        val filteredData = if (validationKeywords != null) {
                            // 1. Logic Filter Kategori (Strict)
                            rawData.filter { product ->
                                val prodName = product.name.lowercase()
                                val prodCat = product.categoryName.lowercase()

                                validationKeywords.any { keyword ->
                                    prodName.contains(keyword) || prodCat.contains(keyword)
                                }
                            }
                        } else {
                            // 2. Logic Filter Pencarian Manual (Query)
                            // [PERBAIKAN] Filter manual berdasarkan query user
                            if (query.isNotBlank()) {
                                val lowerQuery = query.lowercase()
                                rawData.filter { product ->
                                    product.name.lowercase().contains(lowerQuery) ||
                                            product.categoryName.lowercase().contains(lowerQuery)
                                }
                            } else {
                                rawData
                            }
                        }

                        _uiState.update {
                            it.copy(isLoading = false, searchResults = filteredData)
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