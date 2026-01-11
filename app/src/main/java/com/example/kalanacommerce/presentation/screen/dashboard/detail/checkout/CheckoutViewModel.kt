package com.example.kalanacommerce.presentation.screen.dashboard.detail.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalanacommerce.core.util.Resource
import com.example.kalanacommerce.domain.model.CartItem
import com.example.kalanacommerce.domain.model.CheckoutResult
import com.example.kalanacommerce.domain.repository.AddressRepository
import com.example.kalanacommerce.domain.repository.ProductRepository
import com.example.kalanacommerce.domain.usecase.cart.CheckoutUseCase
import com.example.kalanacommerce.domain.usecase.cart.DirectCheckoutUseCase
import com.example.kalanacommerce.domain.usecase.cart.GetCartItemsUseCase
import com.example.kalanacommerce.domain.usecase.cart.UpdateCartItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class CheckoutViewModel(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val checkoutUseCase: CheckoutUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val directCheckoutUseCase: DirectCheckoutUseCase,
    private val productRepository: ProductRepository,
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState = _uiState.asStateFlow()

    private var isDirectCheckout: Boolean = false
    private var directPayload: Pair<String, Int>? = null

    private val _timelineState = MutableStateFlow(PickupTimelineState())
    val timelineState = _timelineState.asStateFlow()

    private val _storeLocationState = MutableStateFlow(
        StoreLocationModel("Memuat Lokasi...", "...", "-")
    )
    val storeLocationState = _storeLocationState.asStateFlow()

    // [DATA MOCK TOKO] Disamakan dengan CartMapper agar sinkron
    // Map: Nama Toko -> Alamat Dummy
    private val storeDatabase = mapOf(
        "Toko Sayur Kalana" to "Jl. Kaliurang Km 5.5, Depok, Sleman",
        "Mitra Tani Sejahtera" to "Jl. Godean Km 7, Godean, Sleman",
        "Segar Abadi Mart" to "Jl. Seturan Raya No. 10, Depok, Sleman",
        "Warung Bu Dewi" to "Jl. Palagan Tentara Pelajar Km 9, Ngaglik, Sleman",
        "Kebun Organik Pak Budi" to "Jl. Anggajaya 2, Condongcatur, Sleman"
    )

    // Load data awal
    init {
        // Timeline & Lokasi akan di-update otomatis saat items ter-load
    }

    fun loadCheckoutItems(payload: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            loadAddresses() // Load alamat user dulu

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

    // --- LOGIC CART ---

    private fun reloadCartData() {
        val currentIds = _uiState.value.checkoutItems.map { it.id }
        loadCartItems(currentIds)
    }

    fun updateQuantity(itemId: String, newQty: Int) {
        viewModelScope.launch {
            updateCartItemUseCase(itemId, newQty).collect { result ->
                if (result is Resource.Success) {
                    reloadCartData()
                }
            }
        }
    }

    private fun loadCartItems(itemIds: List<String>) {
        viewModelScope.launch {
            getCartItemsUseCase().collect { result ->
                if (result is Resource.Success) {
                    val allItems = result.data ?: emptyList()
                    val selectedItems = allItems.filter { it.id in itemIds }

                    _uiState.update { it.copy(isLoading = false, checkoutItems = selectedItems) }

                    // [PENTING] Setelah item dimuat, hitung ulang Timeline & Lokasi berdasarkan toko item tersebut
                    recalculateLogistics(selectedItems)
                }
            }
        }
    }

    private fun loadDirectProductInfo(variantId: String, qty: Int) {
        viewModelScope.launch {
            productRepository.getProducts("").collect { result ->
                if (result is Resource.Success) {
                    val allProducts = result.data ?: emptyList()
                    val product = allProducts.find { p -> p.id == variantId || p.variants.any { v -> v.id == variantId } }

                    if (product != null) {
                        val variant = product.variants.find { it.id == variantId }

                        // [LOGIC SAMA DENGAN CART MAPPER]
                        // Kita generate nama toko yang sama persis logic-nya dengan CartMapper
                        // menggunakan hashCode ID produk agar konsisten.
                        val uniqueSeed = product.id.hashCode()
                        val storeKeys = storeDatabase.keys.toList()
                        // Pakai Math.abs agar index tidak negatif
                        val storeIndex = kotlin.math.abs(uniqueSeed) % storeKeys.size
                        val consistentStoreName = storeKeys[storeIndex]

                        val dummyCartItem = CartItem(
                            id = "TEMP_DIRECT",
                            productId = product.id,
                            productName = product.name,
                            productImage = product.image,
                            productVariantId = variantId,
                            variantName = variant?.name ?: product.variantName,
                            price = variant?.price ?: product.price,
                            quantity = qty,
                            // Pakai nama toko yang konsisten
                            outletName = consistentStoreName,
                            outletId = "mock_id_$storeIndex",
                            stock = 999,
                            maxQuantity = 999
                        )

                        val items = listOf(dummyCartItem)
                        _uiState.update { it.copy(isLoading = false, checkoutItems = items) }

                        // [PENTING] Hitung ulang logistik
                        recalculateLogistics(items)
                    }
                }
            }
        }
    }

    // --- LOGIC LOGISTIK (TIMELINE & LOKASI) ---

    // Fungsi ini dipanggil setiap kali item berubah atau alamat berubah
    private fun recalculateLogistics(items: List<CartItem>) {
        if (items.isEmpty()) return

        // 1. Ambil daftar Nama Toko Unik dari item yang dibeli
        val uniqueStoreNames = items.map { it.outletName }.distinct()
        val storeCount = uniqueStoreNames.size

        // 2. Update Informasi Lokasi Toko
        val locationModel = if (storeCount == 1) {
            // Single Store
            val storeName = uniqueStoreNames.first()
            val address = storeDatabase[storeName] ?: "Alamat tidak tersedia"
            val distance = "${Random.nextInt(1, 5)}.${Random.nextInt(1, 9)} km"

            StoreLocationModel(
                name = storeName,
                address = address,
                distance = distance,
                isMultiStore = false
            )
        } else {
            // Multi Store
            val distanceTotal = "${Random.nextInt(3, 8) * storeCount} km" // Jarak kasar akumulasi
            // Gabungkan nama toko: "Toko A, Toko B"
            val combinedNames = uniqueStoreNames.joinToString(", ")

            StoreLocationModel(
                name = "Ambil dari $storeCount Titik ($combinedNames)",
                address = "Rute gabungan area Sleman",
                distance = distanceTotal,
                isMultiStore = true
            )
        }
        _storeLocationState.value = locationModel

        // 3. Update Timeline (Waktu bertambah seiring jumlah toko)
        // Base time (untuk 1 toko)
        val basePrep = Random.nextInt(5, 10)
        val baseTravel = Random.nextInt(10, 20)

        // Penalti waktu untuk setiap toko tambahan (misal +50% waktu per toko)
        val multiplier = 1 + ((storeCount - 1) * 0.5).toInt()

        val totalPrep = basePrep * multiplier
        val totalTravel = baseTravel * multiplier
        val totalPickup = 2 * storeCount // 2 menit per toko untuk parkir/ambil

        _timelineState.update {
            PickupTimelineState(
                prepTime = totalPrep,
                travelTime = totalTravel,
                pickupTime = totalPickup,
                totalTime = totalPrep + totalTravel + totalPickup,
                storeCount = storeCount
            )
        }
    }

    // Trigger saat ganti alamat (Mungkin jarak berubah, jadi refresh timeline)
    fun selectAddress(address: AddressUiModel) {
        _uiState.update { it.copy(selectedAddress = address) }
        // Refresh logistik dengan items yang ada
        recalculateLogistics(_uiState.value.checkoutItems)
    }

    // ... (Fungsi placeOrder, handleCheckoutResult, loadAddresses SAMA SEPERTI SEBELUMNYA) ...
    // Pastikan tetap menyertakan fungsi-fungsi tersebut agar tidak error.

    fun placeOrder() {
        if (_uiState.value.checkoutItems.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            if (isDirectCheckout && directPayload != null) {
                directCheckoutUseCase(directPayload!!.first, directPayload!!.second).collect { handleCheckoutResult(it) }
            } else {
                val itemIds = _uiState.value.checkoutItems.map { it.id }
                checkoutUseCase(itemIds).collect { handleCheckoutResult(it) }
            }
        }
    }

    private fun handleCheckoutResult(result: Resource<List<CheckoutResult>>) {
        when (result) {
            is Resource.Success -> _uiState.update { it.copy(isLoading = false, checkoutResult = result.data?.firstOrNull()) }
            is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
            is Resource.Loading -> {}
        }
    }

    fun loadAddresses() {
        viewModelScope.launch {
            addressRepository.getAddresses().collect { result ->
                if (result is Resource.Success) {
                    val rawAddresses = result.data ?: emptyList()
                    val uiAddresses = rawAddresses.map {
                        AddressUiModel(it.id, it.recipientName, it.phoneNumber, it.fullAddress, it.isDefault, it.label)
                    }
                    val currentSelected = _uiState.value.selectedAddress
                    val defaultAddr = uiAddresses.find { it.isMain } ?: uiAddresses.firstOrNull()
                    _uiState.update { it.copy(availableAddresses = uiAddresses, selectedAddress = currentSelected ?: defaultAddr) }
                }
            }
        }
    }

    fun onPaymentNavigationHandled() {
        _uiState.update { it.copy(checkoutResult = null) }
    }
}