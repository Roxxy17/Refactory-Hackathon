package com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.profilepage

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet // Icon Dompet
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.kalanacommerce.R
import com.example.kalanacommerce.presentation.components.CustomToast
import com.example.kalanacommerce.presentation.components.ToastType
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfilePage(
    onBack: () -> Unit,
    viewModel: EditProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Helper untuk Format Rupiah
    fun formatRupiah(amountString: String?): String {
        // Konversi String "50000" ke Long 50000, kalau null/gagal jadi 0
        val amount = amountString?.toLongOrNull() ?: 0L

        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(amount).replace("Rp", "Rp ")
    }

    // --- SETUP PHOTO PICKER ---
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { viewModel.updatePhoto(context, it) }
        }
    )

    // --- TOAST STATE ---
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var toastType by remember { mutableStateOf(ToastType.Success) }

    // --- EFFECT ---
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            toastMessage = it
            toastType = ToastType.Success
            showToast = true
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            toastMessage = it
            toastType = ToastType.Error
            showToast = true
            viewModel.clearMessages()
        }
    }



    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(id = R.string.edit_profile_title),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- FOTO PROFIL ---
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.profileImage != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(uiState.profileImage)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .offset(x = 4.dp, y = 4.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable {
                                singlePhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isLoading && uiState.profileImage == null) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Outlined.CameraAlt,
                                contentDescription = "Ubah Foto",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- INPUT FIELDS ---

                // 1. SALDO (BALANCE) - Read Only & Highlighted
                CustomTextField(
                    value = formatRupiah(uiState.balance),
                    onValueChange = { },
                    label = "Saldo / Balance", // Atau stringResource(R.string.balance)
                    icon = Icons.Default.AccountBalanceWallet,
                    enabled = false, // Tidak bisa diedit
                    isHighlight = true // Opsi tambahan (baca penjelasan di bawah)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 2. NAMA
                CustomTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.onNameChange(it) },
                    label = stringResource(R.string.full_name),
                    icon = Icons.Outlined.Person
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 3. EMAIL (Read Only)
                CustomTextField(
                    value = uiState.email,
                    onValueChange = { },
                    label = stringResource(R.string.email), // Pastikan ini ada di strings.xml
                    icon = Icons.Outlined.Email,
                    keyboardType = KeyboardType.Email,
                    enabled = false
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 4. TELEPON
                CustomTextField(
                    value = uiState.phoneNumber,
                    onValueChange = { viewModel.onPhoneChange(it) },
                    label = stringResource(R.string.phone_number),
                    icon = Icons.Outlined.Phone,
                    keyboardType = KeyboardType.Phone
                )

                Spacer(modifier = Modifier.height(40.dp))

                // --- TOMBOL SIMPAN ---
                Button(
                    onClick = { viewModel.updateProfile() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (uiState.isLoading && uiState.profileImage != null) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            stringResource(R.string.save_changes),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        CustomToast(
            message = toastMessage,
            isVisible = showToast,
            type = toastType,
            onDismiss = { showToast = false }
        )
    }
}

// Update Komponen CustomTextField untuk handle Highlight/Color
@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    isHighlight: Boolean = false // Parameter baru
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = if(isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = if (isHighlight) {
            LocalTextStyle.current.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            LocalTextStyle.current
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = if (isHighlight) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            disabledBorderColor = if (isHighlight) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            disabledTextColor = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            disabledLabelColor = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    )
}