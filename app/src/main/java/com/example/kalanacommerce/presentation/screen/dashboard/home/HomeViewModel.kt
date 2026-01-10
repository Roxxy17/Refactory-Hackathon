package com.example.kalanacommerce.presentation.screen.dashboard.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.Category
import com.example.kalanacommerce.domain.model.Product
import com.example.kalanacommerce.domain.usecase.cart.AddToCartUseCase // [NEW] Inject ini
import com.example.kalanacommerce.domain.usecase.product.GetCategoriesUseCase
import com.example.kalanacommerce.domain.usecase.product.GetProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val getCartItemsUseCase: com.example.kalanacommerce.domain.usecase.cart.GetCartItemsUseCase// [NEW] UseCase Cart
) : ViewModel() {

    // Pastikan HomeUiState punya field: navigateToCheckoutWithId: String? = null
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData(isPullRefresh: Boolean = false) {
        if (isPullRefresh) {
            _uiState.update { it.copy(isRefreshing = true) }
        } else {
            if (_uiState.value.products.isEmpty()) {
                _uiState.update { it.copy(isLoading = true) }
            }
        }

        // 1. Categories
        viewModelScope.launch {
            getCategoriesUseCase().collect { result ->
                if (result is Resource.Success) {
                    val allCategory = Category(id = "ALL", name = "Semua")
                    val list = listOf(allCategory) + (result.data ?: emptyList())
                    _uiState.update { it.copy(categories = list) }
                }
            }
        }

        // 2. Products
        viewModelScope.launch {
            getProductsUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val allData = result.data ?: emptyList()
                        // Randomize for Home Display
                        val randomProducts = allData.shuffled().take(10)
                        val msg = if (isPullRefresh) "Beranda diperbarui" else null

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                products = randomProducts,
                                displayProducts = randomProducts,
                                successMessage = msg
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, isRefreshing = false, error = result.message)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    // --- [NEW LOGIC] ADD TO CART & BUY NOW FROM CARD ---

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
        // Cari varian termurah/default
        val lowestVariant = product.variants.minByOrNull { it.price }
        val targetVariantId = lowestVariant?.id ?: product.variants.firstOrNull()?.id ?: product.id

        val navigationPayload = "DIRECT__${targetVariantId}__${quantity}"

        _uiState.update {
            it.copy(navigateToCheckoutWithId = navigationPayload)
        }
    }

    private fun processCartAction(product: Product, quantity: Int, isBuyNow: Boolean) {
        val lowestVariant = product.variants.minByOrNull { it.price }
        val targetVariantId = lowestVariant?.id ?: product.variants.firstOrNull()?.id ?: product.id

        viewModelScope.launch {
            // Gunakan quantity dari parameter, bukan hardcode 1
            addToCartUseCase(targetVariantId, quantity).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        if (isBuyNow) {
                            findCartItemIdAndNavigate(targetVariantId)
                        } else {
                            _uiState.update { it.copy(successMessage = "Berhasil menambahkan $quantity item ke keranjang!") }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(error = result.message) }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    private fun findCartItemIdAndNavigate(variantId: String) {
        viewModelScope.launch {
            getCartItemsUseCase().collect { result ->
                if (result is Resource.Success) {
                    val cartItems = result.data ?: emptyList()
                    // Cari item di keranjang yang product/variant ID-nya sama dengan yang baru kita add
                    // Asumsi: cartItem punya field productVariantId atau sejenisnya.
                    // Jika cartItem.id adalah unik row ID, kita filter berdasarkan relasi produknya.

                    // Logic aman: Ambil item paling baru atau yang cocok ID variannya
                    val foundItem = cartItems.find {
                        it.productVariantId == variantId || it.productId == variantId
                    }

                    if (foundItem != null) {
                        _uiState.update { it.copy(navigateToCheckoutWithId = foundItem.id) }
                    } else {
                        // Fallback jika tidak ketemu (jarang terjadi), navigate pakai variantId (semoga checkout handle)
                        _uiState.update { it.copy(navigateToCheckoutWithId = variantId) }
                    }
                }
            }
        }
    }

    // Reset One-time events
    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null, navigateToCheckoutWithId = null) }
    }

    // ... (Fungsi Filter & Search tetap sama) ...
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
            val matchesCategory = if (catId == "ALL") true else product.categoryName == getCategoryNameById(catId)
            matchesSearch && matchesCategory
        }
        _uiState.update { it.copy(displayProducts = filtered) }
    }

    private fun getCategoryNameById(id: String): String {
        return _uiState.value.categories.find { it.id == id }?.name ?: ""
    }
}