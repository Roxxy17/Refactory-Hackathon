package com.example.kalanacommerce.presentation.screen.dashboard.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.ProductVariant
import com.example.kalanacommerce.domain.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailProductViewModel(
    private val repository: ProductRepository
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

                            // Hitung harga awal berdasarkan varian default
                            val initialPrice = defaultVariant?.price ?: foundProduct.price

                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    product = foundProduct,
                                    selectedVariant = defaultVariant,
                                    relatedProducts = related,
                                    quantity = 1,
                                    totalPrice = initialPrice // Harga 1 item
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
        val currentQty = _uiState.value.quantity
        _uiState.update {
            it.copy(
                selectedVariant = variant,
                // Update total harga langsung berdasarkan harga varian baru * qty
                totalPrice = variant.price * currentQty
            )
        }
    }

    fun incrementQuantity() {
        // Gunakan harga dari varian jika ada, jika tidak pakai harga base product
        val basePrice = _uiState.value.selectedVariant?.price
            ?: _uiState.value.product?.price
            ?: 0L

        val newQty = _uiState.value.quantity + 1
        _uiState.update {
            it.copy(
                quantity = newQty,
                totalPrice = basePrice * newQty
            )
        }
    }

    fun decrementQuantity() {
        val basePrice = _uiState.value.selectedVariant?.price
            ?: _uiState.value.product?.price
            ?: 0L

        if (_uiState.value.quantity > 1) {
            val newQty = _uiState.value.quantity - 1
            _uiState.update {
                it.copy(
                    quantity = newQty,
                    totalPrice = basePrice * newQty
                )
            }
        }
    }
}