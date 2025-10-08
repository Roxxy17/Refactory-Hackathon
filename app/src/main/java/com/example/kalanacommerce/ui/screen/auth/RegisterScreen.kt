package com.example.kalanacommerce.ui.screen.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kalanacommerce.R
import com.example.kalanacommerce.ui.viewmodel.RegisterViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onContinue: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val primaryColor = Color(0xFF069C6F)
    val lightGray = Color(0xFFF1F1F1)
    val darkTextColor = Color(0xFF555555)
    val errorColor = Color(0xFFD32F2F)

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isRegistered, uiState.error) {
        when {
            uiState.isRegistered -> {
                Toast.makeText(context, "✅ Registrasi Berhasil!", Toast.LENGTH_LONG).show()
                onContinue()
                viewModel.resetState()
            }
            uiState.error != null -> {
                Toast.makeText(context, "❌ Gagal: ${uiState.error}", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
        }
    }

    // --- State untuk UI ---
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(false) }

    // --- PERBAIKAN 1: Hapus `isLoading` yang terpisah ---
    // val isLoading by viewModel.isLoading.collectAsStateWithLifecycle() // HAPUS BARIS INI
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    val textFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        cursorColor = primaryColor,
        focusedContainerColor = lightGray,
        unfocusedContainerColor = lightGray
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_logo_panjang),
            contentDescription = "Kalana Logo",
            modifier = Modifier.fillMaxWidth(0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Create Your Account",
            style = MaterialTheme.typography.titleLarge,
            color = darkTextColor
        )
        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(visible = errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = errorColor,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // --- Form Pendaftaran ---
        TextField(
            value = fullName,
            onValueChange = { fullName = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Nama Lengkap", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = Color.Gray) },
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = darkTextColor)
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("No. Telepon", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null, tint = Color.Gray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = darkTextColor)
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Email", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = Color.Gray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = darkTextColor)
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Password", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color.Gray) },
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = darkTextColor),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle password visibility", tint = Color.Gray)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = agreeToTerms,
                onCheckedChange = { agreeToTerms = it },
                colors = CheckboxDefaults.colors(checkedColor = primaryColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Saya setuju dengan Syarat & Ketentuan", color = darkTextColor, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(24.dp))

        // --- PERBAIKAN 2: Gunakan uiState.isLoading di sini ---
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(32.dp), color = primaryColor)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                viewModel.register(
                    fullName = fullName,
                    email = email,
                    password = password,
                    phoneNumber = phoneNumber
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
            // --- PERBAIKAN 3: Gunakan uiState.isLoading di sini juga ---
            enabled = !uiState.isLoading
        ) {
            Text("Sign up", fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            modifier = Modifier
                .clickable(onClick = onNavigateToLogin)
                .padding(8.dp),
            text = buildAnnotatedString {
                append("Already have an account? ")
                withStyle(style = SpanStyle(color = primaryColor, fontWeight = FontWeight.Bold)) {
                    append("Sign in")
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        onNavigateToLogin = {},
        onContinue = {}
    )
}
