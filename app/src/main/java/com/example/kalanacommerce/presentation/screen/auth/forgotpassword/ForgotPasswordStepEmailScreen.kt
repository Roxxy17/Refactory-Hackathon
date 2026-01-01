package com.example.kalanacommerce.presentation.screen.auth.forgotpassword

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.R
import org.koin.androidx.compose.koinViewModel
import com.example.kalanacommerce.presentation.theme.KalanaCommerceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordStepEmailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToOtp: (String) -> Unit,
    viewModel: ForgotPasswordViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // --- SETUP ANIMASI BLOB ---
    val blobColor1 = MaterialTheme.colorScheme.primary
    val blobColor2 = MaterialTheme.colorScheme.secondary
    val backgroundColor = MaterialTheme.colorScheme.background

    val infiniteTransition = rememberInfiniteTransition(label = "Infinite BG")

    val blob1Scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse),
        label = "Blob1"
    )

    val blob2Offset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 50f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Reverse),
        label = "Blob2"
    )

    // --- ROOT CONTAINER (BOX) ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // --- LAYER 1: BACKGROUND ANIMATION ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Blob Atas Kiri (Alpha 0.4f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(blobColor1.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(0f, 0f),
                    radius = size.width * 0.8f * blob1Scale
                ),
                center = Offset(0f, 0f),
                radius = size.width * 0.8f * blob1Scale
            )

            // Blob Bawah Kanan (Alpha 0.5f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(blobColor2.copy(alpha = 0.5f), Color.Transparent),
                    center = Offset(size.width, size.height),
                    radius = size.width * 0.9f
                ),
                center = Offset(size.width - blob2Offset, size.height + blob2Offset),
                radius = size.width * 0.9f
            )
        }

        // --- LAYER 2: KONTEN UTAMA ---
        Scaffold(
            containerColor = Color.Transparent, // PENTING: Transparan agar blob terlihat
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            modifier = Modifier.imePadding()
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

                // --- STATE 1: FORM INPUT EMAIL ---
                AnimatedVisibility(visible = !uiState.isSuccess, enter = fadeIn(), exit = fadeOut()) {
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        IconHeader(Icons.Filled.LockReset, MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = stringResource(R.string.forgot_password_title),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.forgot_password_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(40.dp))

                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = { viewModel.onEmailChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.email_address)) },
                            placeholder = { Text(stringResource(R.string.email_placeholder)) },
                            leadingIcon = { Icon(Icons.Outlined.Email, null) },
                            trailingIcon = {
                                if (uiState.email.isNotEmpty()) {
                                    IconButton({ viewModel.onEmailChange("") }) { Icon(Icons.Filled.Clear, null) }
                                }
                            },
                            isError = !uiState.isEmailValid,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.onSubmit() },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = !uiState.isLoading && uiState.email.isNotEmpty()
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                            } else {
                                Text(stringResource(R.string.send_reset_link))
                            }
                        }
                    }
                }

                // --- STATE 2: EMAIL SENT SUCCESS ---
                AnimatedVisibility(visible = uiState.isSuccess, enter = fadeIn(), exit = fadeOut()) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconHeader(Icons.Outlined.MarkEmailRead, MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = stringResource(R.string.check_your_email),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.email_sent_desc, uiState.email),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(40.dp))

                        Button(
                            onClick = { onNavigateToOtp(uiState.email) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Masukkan Kode OTP")
                        }
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
        ForgotPasswordStepEmailScreen(onNavigateBack = {}, onNavigateToOtp = {})
    }
}