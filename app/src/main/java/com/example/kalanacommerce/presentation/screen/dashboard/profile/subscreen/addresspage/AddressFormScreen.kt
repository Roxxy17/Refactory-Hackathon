package com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.addresspage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.R
import com.example.kalanacommerce.presentation.components.CustomToast
import com.example.kalanacommerce.presentation.components.ToastType
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressFormScreen(
    addressId: String? = null,
    viewModel: AddressViewModel = koinViewModel(),
    onBack: () -> Unit,
    onNavigateToMapPicker: (Double, Double) -> Unit,
    navController: androidx.navigation.NavController
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

    // ID Wilayah
    var provinceId by remember { mutableStateOf("") }
    var cityId by remember { mutableStateOf("") }
    var districtId by remember { mutableStateOf("") }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }
    var isDefault by remember { mutableStateOf(false) }

    // --- MAP RESULT LISTENER ---
    // Mengambil hasil dari MapPickerScreen via SavedStateHandle
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val mapResult by savedStateHandle?.getLiveData<String>("location_result")?.observeAsState() ?: mutableStateOf(null)

    LaunchedEffect(mapResult) {
        mapResult?.let { resultString ->
            val parts = resultString.split(",")
            if (parts.size == 2) {
                latitude = parts[0].toDoubleOrNull() ?: 0.0
                longitude = parts[1].toDoubleOrNull() ?: 0.0
                // Hapus data agar tidak trigaer ulang
                savedStateHandle?.remove<String>("location_result")
            }
        }
    }

    // --- LOAD DATA (EDIT MODE) ---
    LaunchedEffect(addressId) {
        if (addressId != null) {
            viewModel.loadAddressDetail(addressId)
        }
    }

    LaunchedEffect(uiState.selectedAddress) {
        val data = uiState.selectedAddress
        if (data != null && isEditMode) {
            label = data.label
            recipientName = data.recipientName
            phoneNumber = data.phoneNumber
            street = data.street
            postalCode = data.postalCode
            provinceId = data.provincesId
            cityId = data.citiesId
            districtId = data.districtsId
            isDefault = data.isDefault
            latitude = data.latitude
            longitude = data.longitude
        }
    }

    // --- HANDLE TOAST ---
    var showToast by remember { mutableStateOf(false) }
    var toastMsg by remember { mutableStateOf("") }
    var toastType by remember { mutableStateOf(ToastType.Success) }

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            toastMsg = uiState.successMessage!!
            toastType = ToastType.Success
            showToast = true
            kotlinx.coroutines.delay(1500)
            viewModel.clearMessages()
            onBack()
        }
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            toastMsg = uiState.error!!
            toastType = ToastType.Error
            showToast = true
            viewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) stringResource(R.string.title_edit_address) else stringResource(R.string.title_add_address),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.btn_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            // Tombol Simpan Floating
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
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
                                provinceId = provinceId.ifEmpty { "ID-PROV-DUMMY" },
                                cityId = cityId.ifEmpty { "ID-CITY-DUMMY" },
                                districtId = districtId.ifEmpty { "ID-DIS-DUMMY" },
                                isDefault = isDefault,
                                latitude = latitude,
                                longitude = longitude
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEditMode) stringResource(R.string.btn_save_changes) else stringResource(R.string.btn_save_address),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // --- SECTION 1: LABEL ---
            FormSection(
                title = stringResource(R.string.label_address_label),
                icon = Icons.Outlined.Label
            ) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text(stringResource(R.string.hint_address_label)) },
                    placeholder = { Text("Contoh: Rumah, Kantor, Kost") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    colors = inputColors()
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // --- SECTION 2: KONTAK PENERIMA ---
            FormSection(
                title = stringResource(R.string.section_recipient_info),
                icon = Icons.Outlined.Person
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = recipientName,
                        onValueChange = { recipientName = it },
                        label = { Text(stringResource(R.string.label_full_name)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        colors = inputColors()
                    )

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { if (it.all { char -> char.isDigit() }) phoneNumber = it },
                        label = { Text(stringResource(R.string.label_phone_number)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Phone, null) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        colors = inputColors()
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // --- SECTION 3: DETAIL LOKASI ---
            FormSection(
                title = stringResource(R.string.section_location_detail),
                icon = Icons.Outlined.Place
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = street,
                        onValueChange = { street = it },
                        label = { Text(stringResource(R.string.label_full_address)) },
                        placeholder = { Text(stringResource(R.string.placeholder_full_address)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Home, null) },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        ),
                        colors = inputColors()
                    )

                    OutlinedTextField(
                        value = postalCode,
                        onValueChange = { if (it.length <= 5 && it.all { c -> c.isDigit() }) postalCode = it },
                        label = { Text(stringResource(R.string.label_postal_code)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.MarkunreadMailbox, null) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        colors = inputColors()
                    )

                    // [IMPROVED MAP BUTTON UI]
                    val isLocationSet = latitude != 0.0 && longitude != 0.0
                    val mapBorderColor = if (isLocationSet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    val mapContainerColor = if (isLocationSet) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else Color.Transparent

                    Surface(
                        onClick = { onNavigateToMapPicker(latitude, longitude) },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, mapBorderColor),
                        color = mapContainerColor,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isLocationSet) Icons.Default.CheckCircle else Icons.Default.Map,
                                contentDescription = null,
                                tint = if (isLocationSet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isLocationSet) "Lokasi Terpilih" else "Pilih Titik di Peta",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isLocationSet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                if (isLocationSet) {
                                    Text(
                                        text = "${String.format("%.5f", latitude)}, ${String.format("%.5f", longitude)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    Text(
                                        text = "Atur pinpoint untuk pengiriman akurat",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // --- SECTION 4: DATA WILAYAH ---
            FormSection(
                title = stringResource(R.string.section_area_id),
                icon = Icons.Outlined.Map
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Info Wilayah (Otomatis/Manual)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        OutlinedTextField(
                            value = provinceId,
                            onValueChange = { provinceId = it },
                            label = { Text(stringResource(R.string.label_province_id)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = { Icon(Icons.Default.LocationCity, null) },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            colors = inputColors()
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = cityId,
                                onValueChange = { cityId = it },
                                label = { Text(stringResource(R.string.label_city_id)) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = { Icon(Icons.Default.LocationCity, null) },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                colors = inputColors()
                            )

                            OutlinedTextField(
                                value = districtId,
                                onValueChange = { districtId = it },
                                label = { Text(stringResource(R.string.label_district_id)) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                leadingIcon = { Icon(Icons.Default.HolidayVillage, null) },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                colors = inputColors()
                            )
                        }
                    }
                }
            }

            // --- SECTION 5: UTAMA SWITCH ---
            val switchBorderColor = if (isDefault) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            val switchContainerColor = if (isDefault) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = switchContainerColor),
                border = BorderStroke(1.dp, switchBorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { isDefault = !isDefault }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.title_set_main_address),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.desc_set_main_address),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isDefault,
                        onCheckedChange = { isDefault = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        CustomToast(
            message = toastMsg,
            isVisible = showToast,
            type = toastType,
            onDismiss = { showToast = false }
        )
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun FormSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
        content()
    }
}

@Composable
fun inputColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
)