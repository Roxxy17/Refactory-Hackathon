package com.example.kalanacommerce.presentation.screen.start

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.R
import com.example.kalanacommerce.presentation.theme.KalanaCommerceTheme

// Enum untuk status animasi masuk
private enum class ScreenState { Idle, Entered }

@Composable
fun GetStarted(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // --- 1. AMBIL WARNA DARI TEMA (DYNAMIC) ---
    // Warna ini otomatis berubah sesuai Light/Dark mode dari Theme.kt
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground

    // Variasi warna untuk blob (dekorasi)
    val blobColor1 = MaterialTheme.colorScheme.primary
    // Menggunakan secondary atau primaryContainer agar tetap senada dengan tema
    val blobColor2 = MaterialTheme.colorScheme.secondary

    // --- 2. ENTRY TRANSITION CONFIG ---
    var currentState by remember { mutableStateOf(ScreenState.Idle) }
    val transition = updateTransition(targetState = currentState, label = "Screen Entry")

    LaunchedEffect(Unit) {
        currentState = ScreenState.Entered
    }

    // Animasi Alpha untuk Logo
    val logoAlpha by transition.animateFloat(
        label = "Logo Alpha",
        transitionSpec = { tween(800, 100) }
    ) { if (it == ScreenState.Entered) 1f else 0f }

    // Animasi Scale untuk Logo (Zoom in dikit)
    val logoScale by transition.animateFloat(
        label = "Logo Scale",
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioMediumBouncy) }
    ) { if (it == ScreenState.Entered) 1f else 0.8f }

    // Animasi Slogan (Slide Up + Fade)
    val textAlpha by transition.animateFloat(
        label = "Text Alpha",
        transitionSpec = { tween(600, 400) }
    ) { if (it == ScreenState.Entered) 1f else 0f }

    val textOffset by transition.animateDp(
        label = "Text Offset",
        transitionSpec = { tween(600, 400) }
    ) { if (it == ScreenState.Entered) 0.dp else 40.dp }

    // Animasi Button (Slide Up + Fade)
    val buttonsAlpha by transition.animateFloat(
        label = "Buttons Alpha",
        transitionSpec = { tween(500, 600) }
    ) { if (it == ScreenState.Entered) 1f else 0f }

    val buttonsOffset by transition.animateDp(
        label = "Buttons Offset",
        transitionSpec = { tween(500, 600) }
    ) { if (it == ScreenState.Entered) 0.dp else 50.dp }


    // --- 3. INFINITE ANIMATION (Breathing Background) ---
    val infiniteTransition = rememberInfiniteTransition(label = "Infinite BG")

    // Animasi posisi/skala background blob 1
    val blob1Scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse),
        label = "Blob1"
    )

    // Animasi posisi background blob 2
    val blob2Offset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 50f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Reverse),
        label = "Blob2"
    )

    // Animasi Floating Logo (Naik Turun halus)
    val logoFloatingOffset by infiniteTransition.animateFloat(
        initialValue = -10f, targetValue = 10f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "Logo Float"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor) // MENGGUNAKAN WARNA TEMA
    ) {
        // --- 4. BACKGROUND DECORATION (Modern Blobs) ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Blob Atas Kiri
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(blobColor1.copy(alpha = 0.15f), Color.Transparent),
                    center = Offset(0f, 0f),
                    radius = size.width * 0.8f * blob1Scale
                ),
                center = Offset(0f, 0f),
                radius = size.width * 0.8f * blob1Scale
            )

            // Blob Bawah Kanan
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(blobColor2.copy(alpha = 0.2f), Color.Transparent),
                    center = Offset(size.width, size.height),
                    radius = size.width * 0.9f
                ),
                center = Offset(size.width - blob2Offset, size.height + blob2Offset),
                radius = size.width * 0.9f
            )
        }

        // --- 5. MAIN CONTENT ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(1.dp)) // Spacer Dummy atas

            // --- CENTER: Logo & Text ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // Logo Container
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = logoScale
                            scaleY = logoScale
                            alpha = logoAlpha
                            translationY = logoFloatingOffset // Efek mengapung
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo_panjang),
                        contentDescription = "Kalana Logo",
                        modifier = Modifier.width(220.dp)
                        // Opsional: Jika logo Anda hitam pekat dan hilang di dark mode,
                        // Anda bisa menambahkan colorFilter untuk mengubah warnanya:
                        // colorFilter = ColorFilter.tint(textColor)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Typography Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .offset(y = textOffset)
                        .alpha(textAlpha)
                ) {
                    Text(
                        text = "Freshness You Can Trust",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = textColor // MENGGUNAKAN WARNA TEMA
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Experience the best quality products directly\nfrom nature to your doorstep.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = textColor.copy(alpha = 0.7f), // MENGGUNAKAN WARNA TEMA
                            fontWeight = FontWeight.Normal,
                            lineHeight = 24.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            // --- BOTTOM: Buttons ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = buttonsOffset)
                    .alpha(buttonsAlpha),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Main CTA Button
                Button(
                    onClick = onNavigateToRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor, // MENGGUNAKAN WARNA TEMA
                        contentColor = MaterialTheme.colorScheme.onPrimary // Text contrast
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Text(
                        "Get Started",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Secondary Action
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have an account?",
                        color = textColor.copy(alpha = 0.6f), // MENGGUNAKAN WARNA TEMA
                        fontSize = 14.sp
                    )
                    TextButton(
                        onClick = onNavigateToLogin,
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "Sign In",
                            color = primaryColor, // MENGGUNAKAN WARNA TEMA
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun GetStartedPreview() {
    KalanaCommerceTheme(darkTheme = false) { // Coba ganti true untuk preview dark mode
        GetStarted(
            onNavigateToLogin = {},
            onNavigateToRegister = {}
        )
    }
}