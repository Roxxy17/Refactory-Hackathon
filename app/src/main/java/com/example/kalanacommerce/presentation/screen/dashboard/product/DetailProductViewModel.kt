package com.example.kalanacommerce.presentation.screen.dashboard.product

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
    private val addToCartUseCase: AddToCartUseCase // [Inject UseCase Cart]
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailProductUiState())
    val uiState = _uiState.asStateFlow()

    fun loadProductDetail(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. Ambil Semua Produk (untuk detail & related)
            repository.getProducts("").collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val allProducts = result.data ?: emptyList()
                        val foundProduct = allProducts.find { it.id == productId }

                        if (foundProduct != null) {
                            // [LOGIC VARIANT]
                            // Ambil varian pertama sebagai default
                            val defaultVariant = foundProduct.variants.firstOrNull()

                            // [LOGIC RELATED]
                            // Ambil produk lain dengan kategori sama, acak, max 8 item
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
                                    // totalPrice dihapus karena otomatis terhitung di UiState
                                )
                            }
                        } else {
                            _uiState.update { it.copy(isLoading = false, error = "Product not found") }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is Resource.Loading -> { }
                }
            }
        }
    }

    // [BARU] Fungsi Ganti Varian
    fun onVariantSelected(variant: ProductVariant) {
        _uiState.update {
            it.copy(
                selectedVariant = variant
                // Hapus totalPrice = ...
            )
        }
    }

    fun incrementQuantity() {
        val newQty = _uiState.value.quantity + 1
        _uiState.update {
            it.copy(
                quantity = newQty
                // Hapus totalPrice = ...
            )
        }
    }

    fun decrementQuantity() {
        if (_uiState.value.quantity > 1) {
            val newQty = _uiState.value.quantity - 1
            _uiState.update {
                it.copy(
                    quantity = newQty
                    // Hapus totalPrice = ...
                )
            }
        }
    }

    fun addToCart(isBuyNow: Boolean = false) {
        val currentState = _uiState.value
        val product = currentState.product ?: return

        // 1. Validasi Varian
        if (product.variants.isNotEmpty() && currentState.selectedVariant == null) {
            _uiState.update { it.copy(error = "Pilih varian terlebih dahulu") }
            return
        }

        // 2. Tentukan ID yang dikirim
        val targetId = currentState.selectedVariant?.id ?: product.variants.firstOrNull()?.id ?: product.id
        println("DEBUG: Target ID to Cart: $targetId") // Cek output ini di Logcat

        viewModelScope.launch {
            _uiState.update { it.copy(isAddToCartLoading = true, error = null) }

            addToCartUseCase(targetId, currentState.quantity).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        if (isBuyNow) {
                            _uiState.update {
                                it.copy(
                                    isAddToCartLoading = false,
                                    navigateToCheckoutWithId = targetId
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    isAddToCartLoading = false,
                                    addToCartSuccessMessage = "Berhasil masuk keranjang!"
                                )
                            }
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isAddToCartLoading = false,
                                error = result.message
                            )
                        }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    // Reset state notifikasi setelah ditampilkan agar tidak muncul terus
    fun onMessageShown() {
        _uiState.update { it.copy(addToCartSuccessMessage = null, error = null, navigateToCheckoutWithId = null) }
    }
}