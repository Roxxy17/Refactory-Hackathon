package com.example.kalanacommerce.ui.screen.auth

import androidx.compose.animation.core.*
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
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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

// Enum untuk merepresentasikan status layar (telah masuk atau belum)
private enum class ScreenState { Idle, Entered }

@Composable
fun FirstScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val primaryColor = Color(0xFF069C6F)
    val darkTextColor = Color(0xFF333333)

    // --- PERBAIKAN UTAMA: Menggunakan Transition API ---
    var currentState by remember { mutableStateOf(ScreenState.Idle) }
    val transition = updateTransition(targetState = currentState, label = "Screen Entry Transition")

    // Secara otomatis mengubah state menjadi Entered setelah komposisi awal selesai
    LaunchedEffect(Unit) {
        currentState = ScreenState.Entered
    }

    // Definisikan nilai animasi berdasarkan transisi state
    val logoAlpha by transition.animateFloat(
        label = "Logo Alpha",
        transitionSpec = {
            when {
                ScreenState.Idle isTransitioningTo ScreenState.Entered ->
                    tween(durationMillis = 800, delayMillis = 100) // Mulai setelah 100ms
                else -> snap()
            }
        }
    ) { state ->
        if (state == ScreenState.Entered) 1f else 0f
    }

    val sloganAlpha by transition.animateFloat(
        label = "Slogan Alpha",
        transitionSpec = {
            when {
                ScreenState.Idle isTransitioningTo ScreenState.Entered ->
                    tween(durationMillis = 600, delayMillis = 400) // Mulai setelah 400ms
                else -> snap()
            }
        }
    ) { state ->
        if (state == ScreenState.Entered) 1f else 0f
    }

    val sloganOffsetY by transition.animateDp(
        label = "Slogan Offset Y",
        transitionSpec = {
            when {
                ScreenState.Idle isTransitioningTo ScreenState.Entered ->
                    tween(durationMillis = 600, delayMillis = 400)
                else -> snap()
            }
        }
    ) { state ->
        if (state == ScreenState.Entered) 0.dp else 20.dp
    }

    val buttonsAlpha by transition.animateFloat(
        label = "Buttons Alpha",
        transitionSpec = {
            when {
                ScreenState.Idle isTransitioningTo ScreenState.Entered ->
                    tween(durationMillis = 500, delayMillis = 600) // Mulai setelah 600ms
                else -> snap()
            }
        }
    ) { state ->
        if (state == ScreenState.Entered) 1f else 0f
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
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
                        .alpha(logoAlpha) // Terapkan animasi alpha
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your Daily Fashion Discovery.",
                    fontSize = 18.sp,
                    color = darkTextColor.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .offset(y = sloganOffsetY) // Terapkan animasi pergeseran
                        .alpha(sloganAlpha) // Terapkan animasi alpha
                )
            }

            // --- Bagian Tombol ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(buttonsAlpha), // Terapkan animasi alpha pada grup tombol
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
