package com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.R
import com.example.kalanacommerce.presentation.theme.KalanaCommerceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Warna tema untuk blob
    val blobColor1 = MaterialTheme.colorScheme.primary
    val blobColor2 = MaterialTheme.colorScheme.secondary
    val backgroundColor = MaterialTheme.colorScheme.background

    // --- 1. SETUP ANIMASI (Sama seperti LoginScreen) ---
    val infiniteTransition = rememberInfiniteTransition(label = "Infinite BG")

    // Animasi skala blob 1 (Atas Kiri)
    val blob1Scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse),
        label = "Blob1"
    )

    // Animasi posisi blob 2 (Bawah Kanan)
    val blob2Offset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 50f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Reverse),
        label = "Blob2"
    )

    // --- 2. ROOT CONTAINER (BOX) ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // --- 3. BACKGROUND DECORATION (CANVAS) ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Blob Atas Kiri (Lebih Tebal: Alpha 0.4f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(blobColor1.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(0f, 0f),
                    radius = size.width * 0.8f * blob1Scale
                ),
                center = Offset(0f, 0f),
                radius = size.width * 0.8f * blob1Scale
            )

            // Blob Bawah Kanan (Lebih Tebal: Alpha 0.5f)
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

        // --- 4. KONTEN UTAMA (SCAFFOLD) ---
        // Scaffold dibuat transparan agar Blob di belakang terlihat
        Scaffold(
            containerColor = Color.Transparent, // PENTING: Transparan
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.terms_conditions),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.kembali)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent, // PENTING: Header Transparan
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {

                // Tanggal Update
                Text(
                    text = stringResource(R.string.last_updated_december_2025),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Intro
                Text(
                    text = stringResource(R.string.welcome_to_kalana_commerce),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                TermsTextParagraph(
                    stringResource(R.string.stated)
                )

                TermsDivider()

                // Section 1
                TermsSectionTitle(stringResource(R.string.license))
                TermsTextParagraph(
                    stringResource(R.string.license_text)
                )

                // Section 2
                TermsSectionTitle(stringResource(R.string.user_accounts))
                TermsTextParagraph(
                    stringResource(R.string.user_accounts_text)
                )

                // Section 3
                TermsSectionTitle(stringResource(R.string.content_liability))
                TermsTextParagraph(
                    stringResource(R.string.content_liability_text)
                )

                // Section 4
                TermsSectionTitle(stringResource(R.string.your_privacy))
                TermsTextParagraph(
                    stringResource(R.string.your_privacy_text)
                )

                TermsDivider()

                // Footer
                Text(
                    text = stringResource(R.string.question_text),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }
        }
    }
}

// --- Helper Components untuk Konsistensi ---

@Composable
fun TermsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun TermsTextParagraph(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun TermsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 24.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

// --- Preview ---

@Preview(showBackground = true)
@Composable
fun TermsPreviewLight() {
    KalanaCommerceTheme(darkTheme = false) {
        TermsAndConditionsScreen(onBack = {})
    }
}

@Preview(showBackground = true)
@Composable
fun TermsPreviewDark() {
    KalanaCommerceTheme(darkTheme = true) {
        TermsAndConditionsScreen(onBack = {})
    }
}