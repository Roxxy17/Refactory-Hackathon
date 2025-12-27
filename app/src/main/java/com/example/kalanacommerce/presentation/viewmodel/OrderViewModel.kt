package com.example.kalanacommerce.presentation.viewmodel

import android.util.Log // ✅ Import yang diperlukan untuk logging
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.data.model.OrderResponse
import com.example.kalanacommerce.data.model.NewOrderRequest
import com.example.kalanacommerce.back.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class OrderUiState(
    val orders: List<OrderResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val newOrder: OrderResponse? = null // Menyimpan pesanan yang baru dibuat
)

class OrderViewModel(private val repository: OrderRepository) : ViewModel() {

    private val TAG = "ORDER_VIEWMODEL" // Tag untuk Logcat

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState

    init {
        Log.d(TAG, "OrderViewModel initialized. Attempting to load my orders.")
        // Muat daftar pesanan saat ViewModel dibuat
    }

    fun loadMyOrders() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val response = repository.getMyOrders()

                // ✅ LOG HASIL SUKSES GET
                Log.i(TAG, "✅ SUKSES memuat pesanan. Total: ${response.total_orders} pesanan.")

                _uiState.value = _uiState.value.copy(
                    orders = response.orders,
                    isLoading = false
                )
            } catch (e: Exception) {
                // ✅ LOG HASIL GAGAL GET
                Log.e(TAG, "❌ GAGAL memuat pesanan. Error: ${e.message}")

                _uiState.value = _uiState.value.copy(
                    error = "Gagal memuat pesanan: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun checkout(request: NewOrderRequest) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, newOrder = null)
        viewModelScope.launch {
            val result = repository.createOrder(request)

            result.onSuccess { order ->
                // ✅ LOG HASIL SUKSES CHECKOUT
                Log.i(TAG, "✅ CHECKOUT SUKSES! Pesanan ID: ${order.id}. Total: Rp${order.total_amount}.")

                _uiState.value = _uiState.value.copy(
                    newOrder = order,
                    isLoading = false
                )
                loadMyOrders() // Muat ulang daftar setelah checkout berhasil
            }.onFailure { exception ->
                // ✅ LOG HASIL GAGAL CHECKOUT
                Log.e(TAG, "❌ CHECKOUT GAGAL. Error: ${exception.message}")

                _uiState.value = _uiState.value.copy(
                    error = exception.message,
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}