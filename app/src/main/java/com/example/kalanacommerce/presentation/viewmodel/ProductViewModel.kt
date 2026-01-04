package com.example.kalanacommerce.presentation.viewmodel

// com.example.kalanacommerce.ui.viewmodel/ProductViewModel.kt

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.back.NewProductRequest
import com.example.kalanacommerce.back.Product
import com.example.kalanacommerce.back.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val createdProduct: Product? = null // Untuk melacak produk yang baru dibuat
)

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val TAG = "PRODUCT_VIEWMODEL" // Tag untuk Logcat

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState

    init {
        Log.d(TAG, "ProductViewModel initialized. Starting product load.")
        loadProducts()
    }

    // --- FUNGSI GET: MENGAMBIL DAFTAR PRODUK ---
    fun loadProducts() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val response = repository.getProducts()
                Log.i(TAG, "✅ SUKSES memuat produk. Total: ${response.total_products} produk.")

                _uiState.value = _uiState.value.copy(

                    products = response.products,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    // --- FUNGSI POST: MEMBUAT PRODUK BARU ---
    fun createNewProduct(request: NewProductRequest) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, createdProduct = null)
        viewModelScope.launch {
            val result = repository.createProduct(request)

            result.onSuccess { product ->
                _uiState.value = _uiState.value.copy(
                    createdProduct = product,
                    isLoading = false
                )
                // Setelah berhasil membuat, muat ulang daftar
                loadProducts()
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    error = exception.message,
                    isLoading = false
                )
            }
        }
    }

    // Fungsi untuk mereset state error setelah ditampilkan
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
} 