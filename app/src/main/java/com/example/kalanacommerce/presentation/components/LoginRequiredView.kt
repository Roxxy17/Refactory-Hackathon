package com.example.kalanacommerce.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.kalanacommerce.R
import com.example.kalanacommerce.data.local.datastore.ThemeSetting

@Composable
fun LoginRequiredView(
    themeSetting: ThemeSetting,
    onLoginClick: () -> Unit,
    message: String = "Silakan login terlebih dahulu untuk mengakses fitur ini."
) {
    // Logika Tema (Sama seperti screen lain)
    val systemInDark = isSystemInDarkTheme()
    val isDarkActive = remember(themeSetting, systemInDark) {
        when (themeSetting) {
            ThemeSetting.LIGHT -> false
            ThemeSetting.DARK -> true
            ThemeSetting.SYSTEM -> systemInDark
        }
    }

    // Background Image
    val backgroundImage = if (isDarkActive) {
        R.drawable.splash_background_black
    } else {
        R.drawable.splash_background_white
    }

    val textColor = if (isDarkActive) Color.White else Color.Black

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Konten Tengah
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Bisa tambahkan ilustrasi gembok/kunci di sini jika ada asset-nya
            // Icon(Icons.Default.Lock, ...)

            Text(
                text = stringResource(R.string.access_restricted_title),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = if (isDarkActive) Color.Gray else Color.DarkGray
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLoginClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)), // Brand Green
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.7f).height(50.dp)
            ) {
                Text(
                    text = stringResource(R.string.access_restricted_btn),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }
    }
}