package com.example.kalanacommerce.presentation.screen.dashboard.detail.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.ProductVariant
import com.example.kalanacommerce.domain.repository.ProductRepository
import com.example.kalanacommerce.domain.usecase.cart.AddToCartUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailProductViewModel(
    private val repository: ProductRepository,
    private val addToCartUseCase: AddToCartUseCase
    // Dependency 'getCartItemsUseCase' dihapus karena tidak lagi dibutuhkan untuk logika Direct Buy
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailProductUiState())
    val uiState = _uiState.asStateFlow()

    fun loadProductDetail(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getProducts("").collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val allProducts = result.data ?: emptyList()
                        val foundProduct = allProducts.find { it.id == productId }

                        if (foundProduct != null) {
                            // [LOGIC] Pilih Default Variant (Harga Terendah)
                            val defaultVariant = foundProduct.variants.minByOrNull { it.price }
                                ?: foundProduct.variants.firstOrNull()

                            // Related Products logic
                            val related = allProducts
                                .filter { it.categoryName == foundProduct.categoryName && it.id != foundProduct.id }
                                .shuffled()
                                .take(8)

                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    product = foundProduct,
                                    selectedVariant = defaultVariant,
                                    relatedProducts = related,
                                    quantity = 1
                                )
                            }
                        } else {
                            _uiState.update { it.copy(isLoading = false, error = "Produk tidak ditemukan") }
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

    fun onVariantSelected(variant: ProductVariant) {
        _uiState.update { it.copy(selectedVariant = variant) }
    }

    fun incrementQuantity() {
        val newQty = _uiState.value.quantity + 1
        _uiState.update { it.copy(quantity = newQty) }
    }

    fun decrementQuantity() {
        if (_uiState.value.quantity > 1) {
            val newQty = _uiState.value.quantity - 1
            _uiState.update { it.copy(quantity = newQty) }
        }
    }

    // --- FUNGSI 1: BELI LANGSUNG (DIRECT CHECKOUT) ---
    // Tidak memanggil API Cart, tapi langsung navigasi dengan payload khusus
    fun buyNow() {
        val currentState = _uiState.value
        val product = currentState.product ?: return

        // Tentukan ID Varian/Produk
        val targetId = currentState.selectedVariant?.id
            ?: product.variants.minByOrNull { it.price }?.id
            ?: product.id

        // Format payload: "DIRECT__<ID>__<QTY>"
        // String ini nanti akan dibaca oleh CheckoutViewModel
        val navigationPayload = "DIRECT__${targetId}__${currentState.quantity}"

        _uiState.update { it.copy(navigateToCheckoutWithId = navigationPayload) }
    }

    // --- FUNGSI 2: TAMBAH KE KERANJANG ---
    // Memanggil API Cart untuk menyimpan data
    fun addToCart() {
        val currentState = _uiState.value
        val product = currentState.product ?: return

        val targetId = currentState.selectedVariant?.id
            ?: product.variants.minByOrNull { it.price }?.id
            ?: product.id

        viewModelScope.launch {
            _uiState.update { it.copy(isAddToCartLoading = true, error = null) }

            addToCartUseCase(targetId, currentState.quantity).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isAddToCartLoading = false,
                                addToCartSuccessMessage = "Berhasil masuk keranjang"
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(isAddToCartLoading = false, error = result.message)
                        }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    // Reset pesan error/sukses setelah ditampilkan di UI (Toast/Navigasi)
    fun onMessageShown() {
        _uiState.update { it.copy(addToCartSuccessMessage = null, error = null, navigateToCheckoutWithId = null) }
    }
}