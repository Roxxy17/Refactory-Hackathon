package com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.addresspage

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource // <--- Import Penting
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.kalanacommerce.R // <--- Import Resource App kamu
import com.example.kalanacommerce.presentation.components.CustomToast
import com.example.kalanacommerce.presentation.components.ToastType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressFormScreen(
    addressId: String? = null,
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

    // ID Wilayah
    var provinceId by remember { mutableStateOf("") }
    var cityId by remember { mutableStateOf("") }
    var districtId by remember { mutableStateOf("") }

    var isDefault by remember { mutableStateOf(false) }

    // --- LOAD DATA ---
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
        }
    }

    // --- HANDLE RESPONSE ---
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
                        if (isEditMode) stringResource(R.string.title_edit_address)
                        else stringResource(R.string.title_add_address),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                isDefault = isDefault
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
                                if (isEditMode) stringResource(R.string.btn_save_changes)
                                else stringResource(R.string.btn_save_address),
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
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- SECTION 1: LABEL ---
            FormSection(
                title = stringResource(R.string.label_address_label),
                icon = Icons.Outlined.Label
            ) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text(stringResource(R.string.hint_address_label)) }, // Contoh: Rumah...
                    placeholder = { Text(stringResource(R.string.placeholder_address_label)) }, // Simpan sebagai...
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.BookmarkBorder, null) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    ),
                    colors = inputColors()
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

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

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

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
                }
            }

            // --- SECTION 4: DATA WILAYAH (CARD STYLE) ---
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
                            stringResource(R.string.helper_area_id),
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

            // --- SECTION 5: UTAMA SWITCH (CARD STYLE) ---
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDefault) MaterialTheme.colorScheme.primaryContainer.copy(
                        alpha = 0.3f
                    )
                    else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    1.dp,
                    if (isDefault) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                onClick = { isDefault = !isDefault },
                modifier = Modifier.fillMaxWidth()
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
                            stringResource(R.string.title_set_main_address),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            stringResource(R.string.desc_set_main_address),
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

            Spacer(modifier = Modifier.height(24.dp))
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
        content()
    }
}

@Composable
fun inputColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
)