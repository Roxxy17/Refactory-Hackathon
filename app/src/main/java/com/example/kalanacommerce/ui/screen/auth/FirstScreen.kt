package com.example.kalanacommerce.ui.screen.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.R
import com.example.kalanacommerce.ui.theme.KalanaCommerceTheme
import kotlinx.coroutines.delay

@Composable
fun FirstScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val primaryColor = Color(0xFF069C6F)
    val darkTextColor = Color(0xFF333333)

    // --- Animasi Masuk yang Aman (Berjalan Sekali Saja) ---
    val logoAlpha = remember { Animatable(0f) }
    val sloganAlpha = remember { Animatable(0f) }
    val sloganOffsetY = remember { Animatable(20f) } // Posisi awal slogan, sedikit di bawah
    val buttonsAlpha = remember { Animatable(0f) }

    // LaunchedEffect(true) hanya berjalan satu kali saat composable pertama kali dibuat
    LaunchedEffect(key1 = true) {
        // Logo muncul lebih dulu
        logoAlpha.animateTo(1f, animationSpec = tween(durationMillis = 800))

        // Slogan muncul setelah sedikit jeda
        delay(300)
        sloganAlpha.animateTo(1f, animationSpec = tween(durationMillis = 600))
        sloganOffsetY.animateTo(0f, animationSpec = tween(durationMillis = 600))

        // Tombol muncul terakhir
        delay(200)
        buttonsAlpha.animateTo(1f, animationSpec = tween(durationMillis = 500))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                // PENINGKATAN: Latar belakang gradasi halus & statis
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.05f),
                        Color.White,
                        Color.White
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(1.dp))

            // --- Bagian Logo dan Slogan ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_panjang),
                    contentDescription = "Kalana Logo",
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .alpha(logoAlpha.value) // Terapkan animasi alpha
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your Daily Fashion Discovery.",
                    fontSize = 18.sp,
                    color = darkTextColor.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .offset(y = sloganOffsetY.value.dp) // Terapkan animasi pergeseran
                        .alpha(sloganAlpha.value) // Terapkan animasi alpha
                )
            }

            // --- Bagian Tombol ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(buttonsAlpha.value), // Terapkan animasi alpha pada grup tombol
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    onClick = onNavigateToRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp, pressedElevation = 4.dp)
                ) {
                    Text("Get Started", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Get Started Icon",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        text = "Already have an account?",
                        color = darkTextColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, device = "id:pixel_6")
@Composable
fun FirstScreenEnhancedPreview() {
    KalanaCommerceTheme {
        FirstScreen(
            onNavigateToLogin = {},
            onNavigateToRegister = {}
        )
    }
}