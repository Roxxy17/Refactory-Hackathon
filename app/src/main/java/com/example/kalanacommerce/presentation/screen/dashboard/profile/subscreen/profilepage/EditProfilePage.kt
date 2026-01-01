package com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.profilepage

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.presentation.theme.KalanaCommerceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfilePage(onBack: () -> Unit) {
    var name by remember { mutableStateOf("Sinta Dewi") }
    var email by remember { mutableStateOf("sinta.dewi@example.com") }
    var phone by remember { mutableStateOf("081234567890") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profil", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
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
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                // Avatar Circle
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(60.dp)
                    )
                }

                // Edit Icon Badge
                Box(
                    modifier = Modifier
                        .offset(x = 4.dp, y = 4.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { /* Aksi Ganti Foto */ }
                        .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.CameraAlt,
                        contentDescription = "Ubah Foto",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- INPUT FIELDS ---
            CustomTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nama Lengkap",
                icon = Icons.Outlined.Person
            )
            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                icon = Icons.Outlined.Email,
                keyboardType = KeyboardType.Email,
                enabled = false // Email disabled
            )
            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Nomor Telepon",
                icon = Icons.Outlined.Phone,
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- TOMBOL SIMPAN ---
            Button(
                onClick = { /* Simpan */ onBack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Simpan Perubahan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Komponen lokal (bisa dipindah ke folder components jika ingin dipakai di file lain)
@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun EditProfilePreview() {
    KalanaCommerceTheme {
        EditProfilePage(onBack = {})
    }
}