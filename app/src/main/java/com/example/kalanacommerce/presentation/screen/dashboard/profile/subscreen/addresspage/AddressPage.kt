package com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.addresspage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.kalanacommerce.presentation.components.CustomToast // Pakai Toast Custom kamu
import com.example.kalanacommerce.presentation.components.ToastType
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.addresspage.AddressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressListScreen(
    viewModel: AddressViewModel = koinViewModel(),
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle Toast Message
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var toastType by remember { mutableStateOf(ToastType.Success) }

    LaunchedEffect(uiState.error, uiState.successMessage) {
        if (uiState.error != null) {
            toastMessage = uiState.error!!
            toastType = ToastType.Error
            showToast = true
            viewModel.clearMessages()
        }
        if (uiState.successMessage != null) {
            toastMessage = uiState.successMessage!!
            toastType = ToastType.Success
            showToast = true
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Daftar Alamat") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Alamat")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.addresses.isEmpty()) {
                Text("Belum ada alamat tersimpan", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.addresses) { address ->
                        AddressItem(
                            label = address.label,
                            name = address.recipientName,
                            phone = address.phoneNumber,
                            fullAddress = address.fullAddress,
                            isDefault = address.isDefault,
                            onEdit = { onNavigateToEdit(address.id) },
                            onDelete = { viewModel.deleteAddress(address.id) }
                        )
                    }
                }
            }

            // Tampilkan Custom Toast
            CustomToast(
                message = toastMessage,
                isVisible = showToast,
                type = toastType,
                onDismiss = { showToast = false }
            )
        }
    }
}

@Composable
fun AddressItem(
    label: String,
    name: String,
    phone: String,
    fullAddress: String,
    isDefault: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (isDefault) {
                    Spacer(modifier = Modifier.width(8.dp))
                    AssistChip(
                        onClick = {},
                        label = { Text("Utama") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("$name | $phone", style = MaterialTheme.typography.bodyMedium)
            Text(fullAddress, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ubah")
                }
                TextButton(onClick = onDelete, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hapus")
                }
            }
        }
    }
}