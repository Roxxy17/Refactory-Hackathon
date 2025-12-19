package com.example.kalanacommerce.front.screen.start

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
import androidx.compose.ui.draw.scale
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
import com.example.kalanacommerce.front.theme.KalanaCommerceTheme

// Enum untuk status animasi masuk
private enum class ScreenState { Idle, Entered }

@Composable
fun FirstScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // Definisi Warna
    val primaryColor = Color(0xFF069C6F)
    val secondaryColor = Color(0xFF8FD694) // Hijau lebih muda untuk aksen
    val darkTextColor = Color(0xFF333333)

    // --- 1. ENTRY TRANSITION CONFIG ---
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


    // --- 2. INFINITE ANIMATION (Breathing Background) ---
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
            .background(Color.White) // Dasar Putih Bersih
    ) {
        // --- 3. BACKGROUND DECORATION (Modern Blobs) ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Blob Atas Kiri (Hijau Utama Pudar)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.2f), Color.Transparent),
                    center = Offset(0f, 0f),
                    radius = size.width * 0.8f * blob1Scale
                ),
                center = Offset(0f, 0f),
                radius = size.width * 0.8f * blob1Scale
            )

            // Blob Bawah Kanan (Hijau Muda/Kuning Pudar)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(secondaryColor.copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(size.width, size.height),
                    radius = size.width * 0.9f
                ),
                center = Offset(size.width - blob2Offset, size.height + blob2Offset),
                radius = size.width * 0.9f
            )
        }

        // --- 4. MAIN CONTENT ---
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
                            color = darkTextColor
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // --- PERBAIKAN DI SINI ---
                    Text(
                        // Menggunakan \n untuk memaksa baris baru sesuai gambar
                        text = "Experience the best quality products directly\nfrom nature to your doorstep.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = darkTextColor.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Normal,
                            lineHeight = 24.sp // Sedikit dinaikkan agar jarak antar baris enak dibaca
                        ),
                        textAlign = TextAlign.Center,
                        // Padding dikurangi dari 16.dp ke 4.dp agar teks tidak terlalu sempit
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
                        .padding(horizontal = 8.dp), // Sedikit padding kiri kanan
                    shape = RoundedCornerShape(16.dp), // Rounded modern
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor
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
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
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
                        color = darkTextColor.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                    TextButton(
                        onClick = onNavigateToLogin,
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "Sign In",
                            color = primaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Jarak aman dari bawah
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun FirstScreenPreview() {
    KalanaCommerceTheme {
        FirstScreen(
            onNavigateToLogin = {},
            onNavigateToRegister = {}
        )
    }
}