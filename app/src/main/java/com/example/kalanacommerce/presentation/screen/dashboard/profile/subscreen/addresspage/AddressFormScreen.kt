package com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.addresspage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.kalanacommerce.presentation.components.CustomToast
import com.example.kalanacommerce.presentation.components.ToastType
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.addresspage.AddressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressFormScreen(
    addressId: String? = null, // Kalau NULL = Mode Tambah, Kalau ADA ISI = Mode Edit
    viewModel: AddressViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEditMode = addressId != null
    val scrollState = rememberScrollState()

    // --- FORM STATE ---
    var label by remember { mutableStateOf("") }
    var recipientName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }

    // Note: Idealnya Province & City pakai Dropdown (Select),
    // tapi untuk tahap awal kita pakai Text Input dulu biar jalan.
    var provinceId by remember { mutableStateOf("") }
    var cityId by remember { mutableStateOf("") }

    var isDefault by remember { mutableStateOf(false) }

    // --- LOGIKA LOAD DATA (KHUSUS EDIT) ---
    // Efek ini jalan cuma sekali pas layar dibuka
    LaunchedEffect(addressId) {
        if (addressId != null) {
            viewModel.loadAddressDetail(addressId)
        }
    }

    // Efek ini jalan pas data detail berhasil ditarik dari API
    LaunchedEffect(uiState.selectedAddress) {
        val data = uiState.selectedAddress
        if (data != null && isEditMode) {
            label = data.label
            recipientName = data.recipientName
            phoneNumber = data.phoneNumber
            street = data.street
            postalCode = data.postalCode
            // Jika backend kasih nama province, mapping disini.
            // Untuk sekarang asumsi data string ID.
            provinceId = data.provincesId
            cityId = data.citiesId
            isDefault = data.isDefault
        }
    }

    // --- HANDLE SUKSES SIMPAN ---
    var showToast by remember { mutableStateOf(false) }
    var toastMsg by remember { mutableStateOf("") }
    var toastType by remember { mutableStateOf(ToastType.Success) }

    // Jika sukses simpan, muncul toast lalu kembali ke halaman list
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            toastMsg = uiState.successMessage!!
            toastType = ToastType.Success
            showToast = true

            // Delay sebentar biar user baca toast, baru tutup layar
            kotlinx.coroutines.delay(1500)
            viewModel.clearMessages()
            onBack()
        }
    }

    // Jika error
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            toastMsg = uiState.error!!
            toastType = ToastType.Error
            showToast = true
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Ubah Alamat" else "Tambah Alamat") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            // Tombol Simpan di Bawah
            Button(
                onClick = {
                    viewModel.saveAddress(
                        isEdit = isEditMode,
                        addressId = addressId,
                        label = label,
                        name = recipientName,
                        phone = phoneNumber,
                        street = street,
                        postalCode = postalCode,
                        provinceId = provinceId.ifEmpty { "ID-PROV-DUMMY" }, // Fallback biar gak error 422
                        cityId = cityId.ifEmpty { "ID-CITY-DUMMY" },
                        isDefault = isDefault
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (isEditMode) "Simpan Perubahan" else "Simpan Alamat")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. Label Alamat
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Label Alamat (Contoh: Rumah, Kantor)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 2. Nama Penerima & No HP
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = recipientName,
                    onValueChange = { recipientName = it },
                    label = { Text("Nama Penerima") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { if (it.all { char -> char.isDigit() }) phoneNumber = it },
                    label = { Text("No. HP") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }

            // 3. Alamat Lengkap
            OutlinedTextField(
                value = street,
                onValueChange = { street = it },
                label = { Text("Nama Jalan / Gedung / No. Rumah") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            // 4. Wilayah (Sementara Text Input)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = postalCode,
                    onValueChange = { if (it.length <= 5 && it.all { c -> c.isDigit() }) postalCode = it },
                    label = { Text("Kode Pos") },
                    modifier = Modifier.weight(0.4f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Nanti ini diganti Dropdown Selector
                OutlinedTextField(
                    value = cityId,
                    onValueChange = { cityId = it },
                    label = { Text("Kota ID") },
                    modifier = Modifier.weight(0.6f),
                    singleLine = true
                )
            }

            // Nanti ini diganti Dropdown Selector
            OutlinedTextField(
                value = provinceId,
                onValueChange = { provinceId = it },
                label = { Text("Provinsi ID") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 5. Switch Default
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Jadikan Alamat Utama", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Alamat ini akan dipilih otomatis saat checkout",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isDefault,
                    onCheckedChange = { isDefault = it }
                )
            }

            Spacer(modifier = Modifier.height(100.dp)) // Spacer bawah agar tidak tertutup tombol
        }

        // TOAST
        CustomToast(
            message = toastMsg,
            isVisible = showToast,
            type = toastType,
            onDismiss = { showToast = false }
        )
    }
}