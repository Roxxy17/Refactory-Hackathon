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
    private val deleteCartItemUseCase: DeleteCartItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCartItems()
    }

    fun refresh() {
        loadCartItems(isPullToRefresh = true)
    }

    fun loadCartItems(isPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            getCartItemsUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        // [PERBAIKAN] Sorting berdasarkan outletId agar item satu toko berkumpul (Grouping)
                        val items = result.data ?: emptyList()
                        val sortedItems = items.sortedBy { it.outletId }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                cartItems = sortedItems,
                                // Tampilkan pesan sukses hanya jika refresh manual
                                successMessage = if (isPullToRefresh) "Data keranjang diperbarui" else null
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
            // Jika semua sudah terpilih, maka unselect semua. Jika belum, select semua.
            val newSelection = if (state.selectedItemIds.containsAll(allIds) && allIds.isNotEmpty()) {
                emptySet()
            } else {
                allIds
            }
            state.copy(selectedItemIds = newSelection)
        }
    }

    // --- CRUD QUANTITY ---

    fun incrementQuantity(itemId: String, currentQty: Int) {
        updateQuantity(itemId, currentQty + 1)
    }

    fun decrementQuantity(itemId: String, currentQty: Int) {
        if (currentQty > 1) {
            updateQuantity(itemId, currentQty - 1)
        } else {
            // Jika 1 dikurangi, hapus item
            deleteItem(itemId)
        }
    }

    private fun updateQuantity(itemId: String, newQty: Int) {
        viewModelScope.launch {
            updateCartItemUseCase(itemId, newQty).collect { result ->
                if (result is Resource.Success) {
                    loadCartItems() // Reload untuk sinkronisasi harga/total
                } else if (result is Resource.Error) {
                    _uiState.update { it.copy(error = result.message) }
                }
            }
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            deleteCartItemUseCase(itemId).collect { result ->
                if (result is Resource.Success) {
                    _uiState.update {
                        it.copy(
                            selectedItemIds = it.selectedItemIds - itemId,
                            successMessage = "Barang berhasil dihapus"
                        )
                    }
                    loadCartItems()
                } else if (result is Resource.Error) {
                    _uiState.update { it.copy(error = result.message) }
                }
            }
        }
    }

    // --- CHECKOUT NAVIGATION ---

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null, checkoutResult = null) }
    }

    fun onCheckoutClicked() {
        val selectedIds = _uiState.value.selectedItemIds
        if (selectedIds.isEmpty()) {
            _uiState.update { it.copy(error = "Pilih minimal 1 barang untuk checkout") }
            return
        }

        // Logic navigasi ditangani di UI dengan mengecek state,
        // atau Anda bisa menambahkan flag navigasi di sini.
    }

    fun onCheckoutHandled() {
        _uiState.update { it.copy(checkoutResult = null) }
    }
}