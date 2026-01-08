package com.example.kalanacommerce.presentation.screen.dashboard.history.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.usecase.order.GetOrderDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailOrderViewModel(
    private val getOrderDetailUseCase: GetOrderDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailOrderUiState())
    val uiState = _uiState.asStateFlow()

    fun loadOrderDetail(orderId: String) {
        viewModelScope.launch {
            getOrderDetailUseCase(orderId).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> _uiState.update {
                        it.copy(isLoading = false, order = result.data)
                    }
                    is Resource.Error -> _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }
}