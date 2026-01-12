package com.example.kalanacommerce.presentation.screen.dashboard.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.Order
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
                        // Urutkan dari yang terbaru
                        val sortedOrders = result.data?.sortedByDescending { it.date } ?: emptyList()
                        _uiState.update {
                            it.copy(isLoading = false, orders = sortedOrders)
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

        // [PERBAIKAN LOGIC KATEGORI]
        val filteredOrders = when (tabIndex) {
            // Tab 0: "Dalam Proses" -> Khusus MENUNGGU PEMBAYARAN (PENDING)
            0 -> allOrders.filter {
                it.status == OrderStatus.PENDING
            }

            // Tab 1: "Selesai" -> SUDAH BAYAR (PAID), DIPROSES, DIKIRIM, SELESAI
            1 -> allOrders.filter {
                it.status == OrderStatus.PAID ||
                        it.status == OrderStatus.PROCESSED ||
                        it.status == OrderStatus.SHIPPED ||
                        it.status == OrderStatus.COMPLETED
            }

            // Tab 2: "Dibatalkan" -> CANCELLED, FAILED, EXPIRED
            2 -> allOrders.filter {
                it.status == OrderStatus.CANCELLED ||
                        it.status == OrderStatus.FAILED ||
                        it.status == OrderStatus.EXPIRED
            }
            else -> allOrders
        }

        // Lakukan Grouping setelah filter
        val groupedItems = groupOrders(filteredOrders)

        _uiState.update { it.copy(historyItems = groupedItems) }
    }

    private fun groupOrders(orders: List<Order>): List<HistoryUiItem> {
        val result = mutableListOf<HistoryUiItem>()
        val processedIds = mutableSetOf<String>()

        for (order in orders) {
            if (processedIds.contains(order.id)) continue

            val groupId = order.paymentGroupId

            if (groupId == null) {
                result.add(HistoryUiItem.Single(order))
                processedIds.add(order.id)
            } else {
                val groupMembers = orders.filter { it.paymentGroupId == groupId }

                if (groupMembers.size > 1) {
                    result.add(HistoryUiItem.Group(groupId, groupMembers))
                    processedIds.addAll(groupMembers.map { it.id })
                } else {
                    result.add(HistoryUiItem.Single(order))
                    processedIds.add(order.id)
                }
            }
        }
        return result
    }
}