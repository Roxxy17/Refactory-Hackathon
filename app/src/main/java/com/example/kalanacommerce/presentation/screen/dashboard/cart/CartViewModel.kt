package com.example.kalanacommerce.presentation.screen.dashboard.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.usecase.cart.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val deleteCartItemUseCase: DeleteCartItemUseCase,
    private val checkoutUseCase: CheckoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCartItems()
    }

    fun loadCartItems() {
        viewModelScope.launch {
            getCartItemsUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                cartItems = result.data ?: emptyList()
                            )
                        }
                    }
                    is Resource.Error -> _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    // --- LOGIC SELEKSI ITEM ---

    fun toggleSelection(itemId: String) {
        _uiState.update { state ->
            val newSelection = if (state.selectedItemIds.contains(itemId)) {
                state.selectedItemIds - itemId
            } else {
                state.selectedItemIds + itemId
            }
            state.copy(selectedItemIds = newSelection)
        }
    }

    fun toggleSelectAll() {
        _uiState.update { state ->
            val allIds = state.cartItems.map { it.id }.toSet()
            val newSelection = if (state.selectedItemIds.size == allIds.size) {
                emptySet() // Unselect all
            } else {
                allIds // Select all
            }
            state.copy(selectedItemIds = newSelection)
        }
    }

    // --- CRUD ---

    fun incrementQuantity(itemId: String, currentQty: Int) {
        updateQuantity(itemId, currentQty + 1)
    }

    fun decrementQuantity(itemId: String, currentQty: Int) {
        if (currentQty > 1) {
            updateQuantity(itemId, currentQty - 1)
        } else {
            // Opsional: Tanya user mau hapus atau tidak
            deleteItem(itemId)
        }
    }

    private fun updateQuantity(itemId: String, newQty: Int) {
        viewModelScope.launch {
            updateCartItemUseCase(itemId, newQty).collect { result ->
                if (result is Resource.Success) {
                    loadCartItems() // Reload data terbaru
                } else if (result is Resource.Error) {
                    // Handle error (misal: show toast)
                }
            }
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            deleteCartItemUseCase(itemId).collect { result ->
                if (result is Resource.Success) {
                    // Hapus juga dari selection jika ada
                    _uiState.update { it.copy(selectedItemIds = it.selectedItemIds - itemId) }
                    loadCartItems()
                }
            }
        }
    }

    // --- CHECKOUT ---

    fun processCheckout() {
        val selectedIds = _uiState.value.selectedItemIds.toList()
        if (selectedIds.isEmpty()) return

        viewModelScope.launch {
            checkoutUseCase(selectedIds).collect { result ->
                when(result) {
                    is Resource.Loading -> _uiState.update { it.copy(isCheckoutLoading = true) }
                    is Resource.Success -> {
                        // Ambil hasil pertama (karena backend return List<CheckoutResult>)
                        val data = result.data?.firstOrNull()
                        _uiState.update {
                            it.copy(
                                isCheckoutLoading = false,
                                checkoutResult = data
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isCheckoutLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun onCheckoutHandled() {
        _uiState.update { it.copy(checkoutResult = null) }
    }
}