package com.example.kalanacommerce.presentation.screen.dashboard.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.CartItem
import com.example.kalanacommerce.domain.repository.ProductRepository
import com.example.kalanacommerce.domain.usecase.cart.CheckoutUseCase
import com.example.kalanacommerce.domain.usecase.cart.DirectCheckoutUseCase
import com.example.kalanacommerce.domain.usecase.cart.GetCartItemsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val checkoutUseCase: CheckoutUseCase,
    private val directCheckoutUseCase: DirectCheckoutUseCase,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState = _uiState.asStateFlow()

    private var isDirectCheckout: Boolean = false
    private var directPayload: Pair<String, Int>? = null

    fun loadCheckoutItems(payload: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            if (payload.startsWith("DIRECT__")) {
                isDirectCheckout = true
                val parts = payload.split("__")
                if (parts.size == 3) {
                    val variantId = parts[1]
                    val qty = parts[2].toIntOrNull() ?: 1
                    directPayload = Pair(variantId, qty)
                    loadDirectProductInfo(variantId, qty)
                }
            } else {
                isDirectCheckout = false
                val itemIds = payload.split(",").filter { it.isNotEmpty() }
                loadCartItems(itemIds)
            }
        }
    }

    private fun loadCartItems(itemIds: List<String>) {
        viewModelScope.launch {
            getCartItemsUseCase().collect { result ->
                if (result is Resource.Success) {
                    val allItems = result.data ?: emptyList()
                    val selectedItems = allItems.filter { it.id in itemIds }

                    // [FIX] Cukup update items saja. Subtotal & Total dihitung otomatis oleh UiState.
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            checkoutItems = selectedItems
                        )
                    }
                }
            }
        }
    }

    private fun loadDirectProductInfo(variantId: String, qty: Int) {
        viewModelScope.launch {
            productRepository.getProducts("").collect { result ->
                if (result is Resource.Success) {
                    val allProducts = result.data ?: emptyList()
                    val product = allProducts.find { p ->
                        p.id == variantId || p.variants.any { v -> v.id == variantId }
                    }

                    if (product != null) {
                        val variant = product.variants.find { it.id == variantId }
                        val finalPrice = variant?.price ?: product.price
                        val finalName = variant?.name ?: product.variantName

                        // [FIX] Dummy Cart Item dengan parameter lengkap sesuai error log
                        val dummyCartItem = CartItem(
                            id = "TEMP_DIRECT",
                            productId = product.id,
                            productName = product.name,
                            productImage = product.image,
                            productVariantId = variantId,
                            variantName = finalName,
                            price = finalPrice,
                            quantity = qty,
                            outletName = product.outlet?.name ?: "Toko Kalana",
                            // outletId = ..., // Hapus ini jika di CartItem tidak ada field outletId

                            // [FIX] Tambahkan nilai default untuk parameter wajib ini
                            stock = 999,
                            maxQuantity = 999
                        )

                        // [FIX] Cukup update items saja. Jangan set subtotal/total manual.
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                checkoutItems = listOf(dummyCartItem)
                            )
                        }
                    }
                }
            }
        }
    }

    // ... (Fungsi placeOrder & handleCheckoutResult SAMA SEPERTI SEBELUMNYA) ...
    fun placeOrder() {
        if (_uiState.value.checkoutItems.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            if (isDirectCheckout && directPayload != null) {
                val (variantId, qty) = directPayload!!
                directCheckoutUseCase(variantId, qty).collect { result ->
                    handleCheckoutResult(result)
                }
            } else {
                val itemIds = _uiState.value.checkoutItems.map { it.id }
                checkoutUseCase(itemIds).collect { result ->
                    handleCheckoutResult(result)
                }
            }
        }
    }

    private fun handleCheckoutResult(result: Resource<List<com.example.kalanacommerce.domain.model.CheckoutResult>>) {
        when (result) {
            is Resource.Success -> {
                val data = result.data?.firstOrNull()
                _uiState.update { it.copy(isLoading = false, checkoutResult = data) }
            }
            is Resource.Error -> {
                _uiState.update { it.copy(isLoading = false, error = result.message) }
            }
            is Resource.Loading -> {}
        }
    }
}