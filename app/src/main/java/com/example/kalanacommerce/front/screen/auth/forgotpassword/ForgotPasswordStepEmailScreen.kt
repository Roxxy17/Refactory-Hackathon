package com.example.kalanacommerce.front.screen.auth.forgotpassword

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Import ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
// Import Theme & Color untuk akses langsung jika perlu
import com.example.kalanacommerce.front.theme.KalanaCommerceTheme
import com.example.kalanacommerce.front.theme.HintColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordStepEmailScreen(
    onNavigateBack: () -> Unit,
    // Inject ViewModel
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    // Ambil state dari ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        // Menggunakan background dari Theme (Putih di Light, Hitam di Dark)
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            // Menggunakan onBackground (Hitam di Light, Putih di Dark)
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        modifier = Modifier.imePadding()
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // --- FORM INPUT VIEW ---
            AnimatedVisibility(
                visible = !uiState.isSuccess,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    IconHeader(
                        icon = Icons.Filled.LockReset,
                        // Otomatis Hijau Branding
                        primaryColor = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Lupa Password?",
                        // Menggunakan Typography dari Type.kt
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Masukkan email yang terdaftar pada akun Anda. Kami akan mengirimkan instruksi untuk mereset password.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = HintColor, // Warna abu netral
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // TextField Email
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Alamat Email") },
                        placeholder = { Text("nama@email.com", color = HintColor) },
                        leadingIcon = {
                            Icon(Icons.Outlined.Email, contentDescription = null, tint = HintColor)
                        },
                        trailingIcon = {
                            if (uiState.email.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onEmailChange("") }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Hapus", tint = HintColor)
                                }
                            }
                        },
                        isError = !uiState.isEmailValid,
                        supportingText = {
                            if (!uiState.isEmailValid) {
                                Text(
                                    text = "Format email tidak valid",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            // Warna Fokus: Hijau Branding
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            // Warna Error: Merah
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            errorLabelColor = MaterialTheme.colorScheme.error,
                            // Warna Normal
                            unfocusedBorderColor = HintColor
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tombol Submit
                    Button(
                        onClick = { viewModel.onSubmit() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary, // Text Putih/Hitam otomatis
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        ),
                        enabled = !uiState.isLoading && uiState.email.isNotEmpty()
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Kirim Link Reset",
                                style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp)
                            )
                        }
                    }
                }
            }

            // --- SUCCESS VIEW (Email Terkirim) ---
            AnimatedVisibility(
                visible = uiState.isSuccess,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconHeader(
                        icon = Icons.Outlined.MarkEmailRead,
                        primaryColor = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Cek Email Anda",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Kami telah mengirimkan link reset password ke:\n${uiState.email}",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = HintColor
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Kembali ke Login", style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = { viewModel.resetState() }) {
                        Text("Kirim ulang link", color = HintColor)
                    }
                }
            }
        }
    }
}

// Komponen Reusable
@Composable
fun IconHeader(
    icon: ImageVector,
    primaryColor: Color
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(primaryColor.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = primaryColor,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordStepEmailPreview() {
    KalanaCommerceTheme {
        ForgotPasswordStepEmailScreen(onNavigateBack = {})
    }
}