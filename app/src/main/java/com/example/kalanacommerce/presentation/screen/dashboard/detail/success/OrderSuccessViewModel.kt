// File: presentation/screen/dashboard/detail/success/OrderSuccessViewModel.kt
package com.example.kalanacommerce.presentation.screen.dashboard.detail.success

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.repository.AddressRepository
import com.example.kalanacommerce.domain.repository.ProductRepository
import com.example.kalanacommerce.domain.usecase.order.GetOrdersUseCase
import com.example.kalanacommerce.domain.usecase.order.UpdatePickupStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class OrderSuccessViewModel(
    private val addressRepository: AddressRepository,
    private val getOrdersUseCase: GetOrdersUseCase,
    private val productRepository: ProductRepository,
    private val updatePickupStatusUseCase: UpdatePickupStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderSuccessUiState())
    val uiState = _uiState.asStateFlow()

    // [FUNGSI UPDATE STATUS PER ITEM]
    fun updateOrderPickupStatus(orderId: String, newStatus: String) {
        Log.d("SIMULASI", "Tombol ditekan untuk OrderID: $orderId -> Status Baru: $newStatus")

        viewModelScope.launch {
            // Panggil API Patch
            updatePickupStatusUseCase(orderId, newStatus).collect { result ->
                when(result) {
                    is Resource.Success -> {
                        Log.d("SIMULASI", "Sukses Update API: ${result.data?.pickupStatus}")

                        // [LOGIC UPDATE LIST] Cari item di list, ganti dengan yang baru, lalu simpan lagi
                        _uiState.update { currentState ->
                            val updatedList = currentState.orders.map { order ->
                                if (order.id == orderId) {
                                    // Update data order ini dengan response terbaru
                                    result.data ?: order.copy(pickupStatus = newStatus)
                                } else {
                                    order // Order lain biarkan tetap sama
                                }
                            }
                            currentState.copy(orders = updatedList) // Trigger UI Refresh
                        }
                    }
                    is Resource.Error -> {
                        Log.e("SIMULASI", "Gagal Update API: ${result.message}")
                        _uiState.update { it.copy(error = result.message) }
                    }
                    is Resource.Loading -> {
                        // Opsional: Handle loading per item
                    }
                }
            }
        }
    }

    fun loadData(navOrderId: String?, groupId: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. Logic Alamat User
            var userGeo: GeoPoint? = null
            addressRepository.getAddresses().collect { result ->
                if (result is Resource.Success) {
                    val targetAddr = result.data?.find { it.isDefault } ?: result.data?.firstOrNull()
                    if (targetAddr != null && targetAddr.latitude != 0.0) {
                        userGeo = GeoPoint(targetAddr.latitude, targetAddr.longitude)
                    }
                }
            }

            // 2. Logic Order (Ambil LIST, bukan Single ID)
            val targetOrders = mutableListOf<com.example.kalanacommerce.domain.model.Order>()
            val targetOutletIds = mutableListOf<String>()

            getOrdersUseCase().collect { result ->
                if (result is Resource.Success) {
                    val allOrders = result.data ?: emptyList()

                    // Filter: Ambil semua order dalam group ini
                    val relevantOrders = if (groupId != null) {
                        allOrders.filter { it.paymentGroupId == groupId }
                    } else {
                        allOrders.filter { it.id == navOrderId }
                    }

                    targetOrders.addAll(relevantOrders)
                    targetOutletIds.addAll(relevantOrders.map { it.outletId }.distinct())
                }
            }

            // 3. Logic Koordinat Toko
            val storeGeos = mutableListOf<GeoPoint>()
            if (targetOutletIds.isNotEmpty()) {
                productRepository.getOutlets().collect { result ->
                    if (result is Resource.Success) {
                        val allOutlets = result.data ?: emptyList()
                        targetOutletIds.forEach { id ->
                            val outlet = allOutlets.find { it.id == id }
                            if (outlet != null) {
                                val lat = outlet.lat?.toDoubleOrNull() ?: 0.0
                                val long = outlet.long?.toDoubleOrNull() ?: 0.0
                                if (lat != 0.0 && long != 0.0) {
                                    storeGeos.add(GeoPoint(lat, long))
                                }
                            }
                        }
                    }
                }
            }

            // Update State Akhir
            _uiState.update {
                it.copy(
                    isLoading = false,
                    userLocation = userGeo ?: GeoPoint(-7.77, 110.37),
                    storeLocations = storeGeos,
                    orders = targetOrders // [PENTING] Masukkan list order ke state
                )
            }
        }
    }
}