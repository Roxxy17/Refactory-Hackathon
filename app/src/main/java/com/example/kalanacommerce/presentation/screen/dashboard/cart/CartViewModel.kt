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
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                cartItems = result.data ?: emptyList(),
                                // [OPSIONAL] Tampilkan toast saat refresh selesai
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
            // Optimistic update (opsional, agar UI cepat) atau tunggu loading
            updateCartItemUseCase(itemId, newQty).collect { result ->
                if (result is Resource.Success) {
                    loadCartItems()
                    // [BARU] Set pesan sukses
                    _uiState.update { it.copy(successMessage = "Jumlah barang berhasil diubah") }
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
                            successMessage = "Barang berhasil dihapus" // [BARU]
                        )
                    }
                    loadCartItems()
                } else if (result is Resource.Error) {
                    _uiState.update { it.copy(error = result.message) }
                }
            }
        }
    }

    // --- CHECKOUT ---
    fun clearMessages() {
        // [UPDATE] Reset juga successMessage
        _uiState.update { it.copy(error = null, successMessage = null, checkoutResult = null) }
    }

    // --- NAVIGASI KE CHECKOUT ---
    fun onCheckoutClicked() {
        val selectedIds = _uiState.value.selectedItemIds
        if (selectedIds.isEmpty()) {
            _uiState.update { it.copy(error = "Pilih minimal 1 barang") }
            return
        }

        // Gabungkan ID jadi string "id1,id2,id3" untuk dikirim ke CheckoutScreen
        val idString = selectedIds.joinToString(",")
        // Simpan di state khusus navigasi (tambahkan field ini di CartUiState jika belum ada, atau pakai event wrapper)
        // Disini kita pakai checkoutResult sementara untuk trigger (atau buat field baru navigateToCheckout)
        // Agar simple, kita asumsikan UI akan observe ini.
    }

    fun onCheckoutHandled() {
        _uiState.update { it.copy(checkoutResult = null) }
    }
}