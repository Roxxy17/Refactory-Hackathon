package com.example.kalanacommerce.presentation.screen.dashboard.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.OrderStatus
import com.example.kalanacommerce.domain.usecase.order.GetOrdersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderHistoryViewModel(
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderHistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchOrders()
    }

    fun fetchOrders() {
        viewModelScope.launch {
            getOrdersUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                orders = result.data ?: emptyList()
                            )
                        }
                        filterOrders(_uiState.value.selectedTab)
                    }
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
        filterOrders(index)
    }

    private fun filterOrders(tabIndex: Int) {
        val allOrders = _uiState.value.orders
        val filtered = when (tabIndex) {
            0 -> allOrders.filter { // Dalam Proses (Pending, Paid, Shipped, Processed)
                it.status == OrderStatus.PENDING ||
                        it.status == OrderStatus.PAID ||
                        it.status == OrderStatus.PROCESSED ||
                        it.status == OrderStatus.SHIPPED
            }
            1 -> allOrders.filter { it.status == OrderStatus.COMPLETED } // Selesai
            2 -> allOrders.filter { it.status == OrderStatus.CANCELLED } // Batal
            else -> allOrders
        }
        _uiState.update { it.copy(filteredOrders = filtered) }
    }
}