package com.example.kalanacommerce.front.screen.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.R
import com.example.kalanacommerce.front.theme.KalanaCommerceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit
) {
    val primaryColor = Color(0xFF069C6F)
    val lightGray = Color(0xFFF1F1F1)
    val darkTextColor = Color(0xFF555555)

    Scaffold(
        // PERBAIKAN: Mengatur warna latar belakang halaman ini menjadi putih
        containerColor = Color.White,

        topBar = {
            TopAppBar(
                title = { Text("Lupa Password") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo_panjang),
                contentDescription = "Kalana Logo",
                modifier = Modifier.fillMaxWidth(0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Jangan khawatir!",
                style = MaterialTheme.typography.headlineSmall,
                color = darkTextColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Masukkan alamat email Anda yang terdaftar untuk menerima link reset password.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            var email by remember { mutableStateOf("") }

            TextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Email", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = Color.Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = primaryColor,
                    focusedContainerColor = lightGray,
                    unfocusedContainerColor = lightGray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* TODO: Aksi untuk mengirim email reset */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text("Kirim Link Reset", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    KalanaCommerceTheme {
        ForgotPasswordScreen(onNavigateBack = {})
    }
}