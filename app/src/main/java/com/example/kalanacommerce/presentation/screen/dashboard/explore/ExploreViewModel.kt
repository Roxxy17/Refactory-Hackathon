package com.example.kalanacommerce.presentation.screen.dashboard.explore

import ExploreUiState
import UiCategory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.Category
import com.example.kalanacommerce.domain.model.Product
import com.example.kalanacommerce.domain.repository.ProductRepository
import com.example.kalanacommerce.domain.usecase.cart.AddToCartUseCase // [NEW]
import com.example.kalanacommerce.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val repository: ProductRepository,
    private val addToCartUseCase: AddToCartUseCase // [NEW] Inject Cart UseCase
) : ViewModel() {

    // Pastikan ExploreUiState punya field: navigateToCheckoutWithId: String? = null & successMessage
    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    // ... (Daftar Static Categories tetap sama) ...
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
        val mappedCategories = staticUiCategories.map { Category(id = it.id, name = it.id) }
        _uiState.update { it.copy(categories = mappedCategories) }
    }

    // --- [NEW LOGIC] ADD TO CART / BUY NOW ---

    fun onAddToCart(product: Product, quantity: Int) {
        // Logika Add Cart tetap sama (panggil API add cart)
        val lowestVariant = product.variants.minByOrNull { it.price }
        val targetVariantId = lowestVariant?.id ?: product.variants.firstOrNull()?.id ?: product.id

        viewModelScope.launch {
            addToCartUseCase(targetVariantId, quantity).collect { result ->
                // ... handle success/error cart ...
                if(result is Resource.Success) {
                    _uiState.update { it.copy(successMessage = "Berhasil masuk keranjang") }
                }
            }
        }
    }

    // [UPDATE] Terima parameter quantity
    fun onBuyNow(product: Product, quantity: Int) {
        val lowestVariant = product.variants.minByOrNull { it.price }
        val targetVariantId = lowestVariant?.id ?: product.variants.firstOrNull()?.id ?: product.id

        // Langsung Navigasi (bypass Cart)
        // Pastikan ExploreUiState punya field navigateToCheckoutWithId
        val navigationPayload = "DIRECT__${targetVariantId}__${quantity}"
        _uiState.update { it.copy(navigateToCheckoutWithId = navigationPayload) } // Asumsi field ini ada
    }

    private fun processCartAction(product: Product, quantity: Int, isBuyNow: Boolean) {
        val lowestVariant = product.variants.minByOrNull { it.price }
        val targetId = lowestVariant?.id ?: product.variants.firstOrNull()?.id ?: product.id

        viewModelScope.launch {
            // Gunakan quantity dari parameter
            addToCartUseCase(targetId, quantity).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        if (isBuyNow) {
                            // Jika nanti ada navigasi checkout, tambahkan di sini
                            // _uiState.update { it.copy(navigateToCheckoutWithId = targetId) }
                        } else {
                            // Pastikan ExploreUiState punya field successMessage jika ingin toast
                            // _uiState.update { it.copy(successMessage = "Berhasil masuk keranjang") }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(error = result.message) }
                    }
                    else -> {}
                }
            }
        }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(error = null) } // Reset navigateToCheckoutWithId juga disini
    }

    // ... (Fungsi Search & Category Selection tetap sama) ...
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query, selectedCategory = null) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isNotEmpty()) {
                delay(500)
                searchProducts(query, validationKeywords = null)
            } else {
                _uiState.update { it.copy(searchResults = emptyList()) }
            }
        }
    }

    fun onCategorySelected(category: Category) {
        _uiState.update { it.copy(selectedCategory = category, searchQuery = "") }
        val config = staticUiCategories.find { it.id == category.id }
        if (config != null) {
            searchProducts(config.apiQuery, config.validationKeywords)
        }
    }

    fun clearSearch() { _uiState.update { it.copy(searchQuery = "", searchResults = emptyList()) } }
    fun clearCategory() { _uiState.update { it.copy(selectedCategory = null, searchResults = emptyList()) } }

    private fun searchProducts(query: String, validationKeywords: List<String>? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getProducts(query).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val rawData = result.data ?: emptyList()
                        val filteredData = if (validationKeywords != null) {
                            rawData.filter { product ->
                                val prodName = product.name.lowercase()
                                val prodCat = product.categoryName.lowercase()
                                validationKeywords.any { keyword -> prodName.contains(keyword) || prodCat.contains(keyword) }
                            }
                        } else {
                            if (query.isNotBlank()) {
                                val lowerQuery = query.lowercase()
                                rawData.filter { product ->
                                    product.name.lowercase().contains(lowerQuery) || product.categoryName.lowercase().contains(lowerQuery)
                                }
                            } else rawData
                        }
                        _uiState.update { it.copy(isLoading = false, searchResults = filteredData) }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                    else -> Unit
                }
            }
        }
    }
}