package com.example.kalanacommerce.presentation.screen.dashboard.history.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.usecase.order.GetOrdersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionGroupViewModel(
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionGroupUiState())
    val uiState = _uiState.asStateFlow()

    fun loadGroupData(paymentGroupId: String) {
        _uiState.update { it.copy(paymentGroupId = paymentGroupId, isLoading = true) }

        viewModelScope.launch {
            // Karena kita belum punya API khusus getOrderByGroupId,
            // kita ambil semua order lalu filter di sisi klien (sementara).
            // Idealnya: Minta Backend buat endpoint GET /orders/group/{paymentGroupId}
            getOrdersUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val groupOrders = result.data?.filter { it.paymentGroupId == paymentGroupId } ?: emptyList()
                        _uiState.update { it.copy(isLoading = false, orders = groupOrders) }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }
}