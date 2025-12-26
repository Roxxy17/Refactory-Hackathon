package com.example.kalanacommerce.front.screen.auth.register

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kalanacommerce.R
import org.koin.androidx.compose.koinViewModel
import com.example.kalanacommerce.front.theme.* // Import tetap ada untuk akses Typography jika perlu

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onContinue: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // --- State Form ---
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(false) }

    // Warna Dinamis
    val backgroundColor = MaterialTheme.colorScheme.background

    // --- Validasi ---
    val isFormValid = remember(name, phoneNumber, email, password, agreeToTerms) {
        name.isNotBlank() && phoneNumber.isNotBlank() && email.isNotBlank() &&
                password.length >= 6 && agreeToTerms
    }

    LaunchedEffect(uiState.isRegistered) {
        if (uiState.isRegistered) {
            Toast.makeText(context, "✅ Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
            onContinue()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor) // MENGGUNAKAN WARNA TEMA
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // 1. HEADER SECTION
            HeaderSection()

            Spacer(modifier = Modifier.height(32.dp))

            // 2. FORM SECTION
            RegisterTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "Full Name",
                icon = Icons.Outlined.Person,
                imeAction = ImeAction.Next,
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RegisterTextField(
                value = phoneNumber,
                onValueChange = { if (it.all { char -> char.isDigit() }) phoneNumber = it },
                placeholder = "Phone Number",
                icon = Icons.Outlined.Phone,
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next,
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RegisterTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email Address",
                icon = Icons.Outlined.Email,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RegisterTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                icon = Icons.Outlined.Lock,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                onDone = { focusManager.clearFocus() },
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. TERMS & CONDITIONS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { agreeToTerms = !agreeToTerms },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agreeToTerms,
                    onCheckedChange = { agreeToTerms = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary, // MENGGUNAKAN WARNA TEMA
                        uncheckedColor = MaterialTheme.colorScheme.outline
                    )
                )
                Text(
                    text = "I agree to the Terms & Conditions",
                    color = if (agreeToTerms) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant, // MENGGUNAKAN WARNA TEMA
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. ERROR MESSAGE & BUTTON
            AnimatedVisibility(
                visible = uiState.message != null && uiState.isError,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = uiState.message ?: "",
                    color = MaterialTheme.colorScheme.error, // MENGGUNAKAN WARNA TEMA
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = { viewModel.register(name, email, password, phoneNumber) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // MENGGUNAKAN WARNA TEMA
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
                enabled = !uiState.isLoading && isFormValid
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text("Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. SOCIAL LOGIN
            SocialLoginSection()

            Spacer(modifier = Modifier.height(16.dp))

            // 6. FOOTER
            FooterSection(onNavigateToLogin)
        }
    }
}

// --- SUB-COMPONENTS (Agar kode utama bersih) ---

@Composable
private fun HeaderSection() {
    Image(
        painter = painterResource(id = R.drawable.ic_logo_panjang),
        contentDescription = "Kalana Logo",
        modifier = Modifier.width(180.dp)
        // Opsional: Jika logo Anda hitam pekat dan hilang di Dark Mode:
        // colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Create Account",
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground // MENGGUNAKAN WARNA TEMA
        )
    )
    Text(
        text = "Sign up to get started!",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant, // MENGGUNAKAN WARNA TEMA
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun SocialLoginSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
            Text(
                text = "or continue with",
                modifier = Modifier.padding(horizontal = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Google Button Container
        Surface(
            onClick = { /* TODO: Google Login */ },
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.surfaceContainerHigh, // Container warna terang/gelap sesuai tema
            modifier = Modifier.size(50.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google Login",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun FooterSection(onLoginClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Already have an account? ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Sign in",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onLoginClick() }
        )
    }
}

// --- REUSABLE TEXT FIELD (Kunci Kode Rapi) ---
@Composable
private fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction,
    onNext: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    val containerColor = MaterialTheme.colorScheme.surfaceVariant // Warna isian TextField
    val contentColor = MaterialTheme.colorScheme.onSurface

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingIcon = { Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        textStyle = LocalTextStyle.current.copy(
            color = contentColor,
            fontSize = 16.sp
        ),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            disabledContainerColor = containerColor,
            errorIndicatorColor = Color.Transparent
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { onNext?.invoke() },
            onDone = { onDone?.invoke() }
        ),
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { onTogglePassword?.invoke() }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle Password",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else null
    )
}

@Preview(showBackground = true, heightDp = 900)
@Composable
fun RegisterScreenPreview() {
    KalanaCommerceTheme(darkTheme = false) {
        RegisterScreen(onNavigateToLogin = {}, onContinue = {})
    }
}