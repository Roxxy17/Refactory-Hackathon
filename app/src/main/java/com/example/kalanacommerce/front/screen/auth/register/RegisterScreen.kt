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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import org.koin.androidx.compose.koinViewModel
import com.example.kalanacommerce.front.theme.*

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
            .background(Color.White)
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .imePadding(), // Penting agar input tidak tertutup keyboard
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
                        checkedColor = PrimaryColor,
                        uncheckedColor = HintColor
                    )
                )
                Text(
                    text = "I agree to the Terms & Conditions",
                    color = if (agreeToTerms) DarkTextColor else HintColor,
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
                    color = ErrorColor,
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
                    containerColor = PrimaryColor,
                    disabledContainerColor = PrimaryColor.copy(alpha = 0.5f)
                ),
                enabled = !uiState.isLoading && isFormValid
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Create Account",
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            color = DarkTextColor
        )
    )
    Text(
        text = "Sign up to get started!",
        style = MaterialTheme.typography.bodyMedium,
        color = HintColor,
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
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text(
                text = "or continue with",
                modifier = Modifier.padding(horizontal = 12.dp),
                color = HintColor,
                fontSize = 12.sp
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Google Button Container
        IconButton(
            onClick = { /* TODO: Google Login */ },
            modifier = Modifier
                .size(50.dp)
                .background(Color.White, shape = RoundedCornerShape(50))
                // Menambahkan border tipis agar terlihat rapi
                .then(Modifier.background(LightGray, RoundedCornerShape(50)))
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "Google Login",
                modifier = Modifier.size(24.dp) // Ukuran icon Google standar
            )
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
            color = HintColor
        )
        Text(
            text = "Sign in",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = PrimaryColor,
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
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = HintColor) },
        leadingIcon = { Icon(icon, null, tint = HintColor) },
        textStyle = LocalTextStyle.current.copy(
            color = DarkTextColor,
            fontSize = 16.sp
        ),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = PrimaryColor,
            focusedContainerColor = LightGray,
            unfocusedContainerColor = LightGray,
            disabledContainerColor = LightGray,
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
                        tint = HintColor
                    )
                }
            }
        } else null
    )
}

@Preview(showBackground = true, heightDp = 900)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onNavigateToLogin = {}, onContinue = {})
}