package com.example.kalanacommerce.front.screen.auth.login

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.example.kalanacommerce.front.theme.*

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onSignInSuccess: () -> Unit,
    viewModel: SignInViewModel = koinViewModel()
) {
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // --- State Input ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    // Validasi sederhana
    val isFormValid = remember(email, password) {
        email.isNotBlank() && password.isNotEmpty()
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSignInSuccess()
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
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // 1. HEADER
            HeaderSection()

            Spacer(modifier = Modifier.height(32.dp))

            // 2. FORM INPUT
            LoginTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = stringResource(id = R.string.email),
                icon = Icons.Outlined.Email,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LoginTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = stringResource(id = R.string.password),
                icon = Icons.Outlined.Lock,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                onDone = {
                    focusManager.clearFocus()
                },
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible }
            )

            // 3. REMEMBER ME & FORGOT PASSWORD (STANDARD)
            // Tetap ada untuk user yang memang lupa password sebelum mencoba login
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryColor,
                            uncheckedColor = HintColor
                        )
                    )
                    Text(
                        text = stringResource(id = R.string.remember_me),
                        color = DarkTextColor,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = stringResource(id = R.string.forgot_password),
                    color = PrimaryColor,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.clickable(onClick = onNavigateToForgotPassword)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. ERROR MESSAGE & CONDITIONAL FORGOT PASSWORD
            // INI BAGIAN YANG DIUBAH
            AnimatedVisibility(
                visible = uiState.error != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    // Teks Error
                    Text(
                        text = uiState.error ?: "",
                        color = ErrorColor,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Teks Forgot Password Tambahan (Muncul saat error)
                    Text(
                        text = "Forgot your password?", // Bisa diganti stringResource
                        color = PrimaryColor, // Warna Hijau agar terlihat sebagai solusi
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .clickable(onClick = onNavigateToForgotPassword)
                            .padding(4.dp) // Touch target area
                    )
                }
            }

            // 5. BUTTON
            Button(
                onClick = { viewModel.signIn(email, password) },
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
                    Text(
                        text = stringResource(id = R.string.sign_in),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 6. SOCIAL LOGIN
            SocialLoginSection()

            Spacer(modifier = Modifier.height(16.dp))

            // 7. FOOTER
            FooterSection(onNavigateToRegister)
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
private fun HeaderSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo_panjang),
            contentDescription = "Kalana Logo",
            modifier = Modifier.width(180.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.login_welcome_back),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = DarkTextColor
            )
        )
        Text(
            text = "Please sign in to your account",
            style = MaterialTheme.typography.bodyMedium,
            color = HintColor,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
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
                text = stringResource(id = R.string.or_continue_with),
                modifier = Modifier.padding(horizontal = 12.dp),
                color = HintColor,
                fontSize = 12.sp
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }
        Spacer(modifier = Modifier.height(16.dp))

        IconButton(
            onClick = { /* TODO: Google Login */ },
            modifier = Modifier
                .size(50.dp)
                .background(Color.White, shape = RoundedCornerShape(50))
                .then(Modifier.background(LightGray, RoundedCornerShape(50)))
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "Google Login",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun FooterSection(onRegisterClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.dont_have_account) + " ",
            style = MaterialTheme.typography.bodyMedium,
            color = HintColor
        )
        Text(
            text = stringResource(id = R.string.sign_up),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = PrimaryColor,
            modifier = Modifier.clickable { onRegisterClick() }
        )
    }
}

@Composable
private fun LoginTextField(
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

@Preview(showBackground = true, heightDp = 800)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onNavigateToRegister = {},
        onNavigateToForgotPassword = {},
        onSignInSuccess = {}
    )
}