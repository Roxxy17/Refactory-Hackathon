package com.example.kalanacommerce.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.R

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit
) {
    // Definisi warna agar mudah diubah
    val primaryColor = Color(0xFF069C6F)
    val lightGray = Color(0xFFF1F1F1)
    val darkTextColor = Color(0xFF555555)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // --- Logo ---
        Image(
            painter = painterResource(id = R.drawable.ic_logo_panjang),
            contentDescription = "Kalana Logo",
            modifier = Modifier.fillMaxWidth(0.7f) // Mengisi 70% lebar layar
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Subtitle ---
        Text(
            text = "Create Your Account",
            style = MaterialTheme.typography.titleLarge,
            color = darkTextColor
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- State untuk input fields ---
        var fullName by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        var rememberMe by remember { mutableStateOf(false) }

        // --- Input Nama Lengkap ---
        TextField(
            value = fullName,
            onValueChange = { fullName = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Nama Lengkap", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = Color.Gray) },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = primaryColor,
                focusedContainerColor = lightGray,
                unfocusedContainerColor = lightGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Input Alamat ---
        TextField(
            value = address,
            onValueChange = { address = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Alamat", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Outlined.Place, contentDescription = null, tint = Color.Gray) },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = primaryColor,
                focusedContainerColor = lightGray,
                unfocusedContainerColor = lightGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Input Email ---
        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Email", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = Color.Gray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = primaryColor,
                focusedContainerColor = lightGray,
                unfocusedContainerColor = lightGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Input Password ---
        TextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Password", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color.Gray) },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = primaryColor,
                focusedContainerColor = lightGray,
                unfocusedContainerColor = lightGray
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description, tint = Color.Gray)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Checkbox "Remember me" ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
                colors = CheckboxDefaults.colors(checkedColor = primaryColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Remember me", color = darkTextColor)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Tombol Sign Up ---
        Button(
            onClick = { /* TODO: Aksi untuk register */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Text("Sign up", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Versi BARU yang sudah diperbaiki
        val interactionSource = remember { MutableInteractionSource() }

        Text(
            modifier = Modifier.clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = true),
                onClick = onNavigateToLogin
            ),
            text = buildAnnotatedString {
                append("Already have an account? ")
                withStyle(style = SpanStyle(color = primaryColor, fontWeight = FontWeight.Bold)) {
                    append("Sign in")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onNavigateToLogin = {})
}
