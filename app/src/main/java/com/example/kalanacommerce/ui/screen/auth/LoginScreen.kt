package com.example.kalanacommerce.ui.screen.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.example.kalanacommerce.R
import com.example.kalanacommerce.ui.viewmodel.SignInViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onSignInSuccess: (token: String) -> Unit,
    viewModel: SignInViewModel = koinViewModel()
) {
    val primaryColor = Color(0xFF069C6F)
    val darkTextColor = Color(0xFF555555)
    val errorColor = Color(0xFFD32F2F)

    // State Input Lokal
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Ambil State dari ViewModel (Sumber Kebenaran Tunggal)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // uiState.error dan uiState.isLoading dari ViewModel yang digunakan.

    // Efek Samping untuk penanganan status login (sukses/gagal)
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            uiState.token?.let(onSignInSuccess)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.05f), Color.White, Color.White)
                )
            )
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {

            // --- Header ---
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_panjang),
                    contentDescription = "Kalana Logo",
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.login_welcome_back),
                    style = MaterialTheme.typography.titleLarge,
                    color = darkTextColor
                )
            }

            // --- Form Login ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // PERBAIKAN UTAMA: Tambahkan Enter/Exit Transition yang eksplisit
                AnimatedVisibility(
                    visible = uiState.error != null,
                    enter = fadeIn(tween(150)) + expandVertically(expandFrom = Alignment.Top),
                    exit = fadeOut(tween(150)) + shrinkVertically(shrinkTowards = Alignment.Top)
                ) {
                    Text(
                        text = uiState.error ?: "",
                        color = errorColor,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }

                AuthTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = stringResource(id = R.string.email),
                    leadingIcon = Icons.Outlined.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    darkTextColor = darkTextColor
                )

                AuthTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = stringResource(id = R.string.password),
                    leadingIcon = Icons.Outlined.Lock,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = "Toggle password visibility", tint = Color.Gray)
                        }
                    },
                    darkTextColor = darkTextColor
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it }, colors = CheckboxDefaults.colors(checkedColor = primaryColor))
                    Text(stringResource(id = R.string.remember_me), color = darkTextColor, style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    text = stringResource(id = R.string.forgot_password),
                    color = primaryColor,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.clickable(onClick = onNavigateToForgotPassword)
                )
            }

            // Gunakan uiState.isLoading
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp), color = primaryColor)
            }

            Button(
                onClick = {
                    viewModel.signIn(email, password)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                // Gunakan uiState.isLoading untuk menonaktifkan tombol
                enabled = !uiState.isLoading
            ) {
                Text(stringResource(id = R.string.sign_in), fontSize = 16.sp, color = Color.White)
            }

            // --- Social Login & Link Pendaftaran ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Divider(modifier = Modifier.weight(1f))
                    Text(stringResource(id = R.string.or_continue_with), modifier = Modifier.padding(horizontal = 8.dp), color = Color.Gray, fontSize = 12.sp)
                    Divider(modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    IconButton(onClick = { /* TODO: Google Login */ }) {
                        Image(painter = painterResource(id = R.drawable.ic_google), contentDescription = "Google Login", modifier = Modifier.size(48.dp))
                    }
                    IconButton(onClick = { /* TODO: Facebook Login */ }) {
                        Image(painter = painterResource(id = R.drawable.ic_meta), contentDescription = "Facebook Login", modifier = Modifier.size(48.dp))
                    }
                }
                Text(
                    modifier = Modifier.clickable(onClick = onNavigateToRegister).padding(8.dp),
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.dont_have_account))
                        append(" ")
                        withStyle(style = SpanStyle(color = primaryColor, fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.sign_up))
                        }
                    }
                )
            }
        }
    }
}

// Komponen TextField yang bisa dipakai ulang (Reusable Component)
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    darkTextColor: Color = Color(0xFF555555),
    primaryColor: Color = Color(0xFF069C6F),
    lightGray: Color = Color(0xFFF1F1F1)
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(label, color = Color.Gray) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = Color.Gray) },
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = primaryColor,
            focusedContainerColor = lightGray,
            unfocusedContainerColor = lightGray
        ),
        textStyle = LocalTextStyle.current.copy(color = darkTextColor)
    )
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun LoginScreenPreview() {
    LoginScreen(onNavigateToRegister = {}, onSignInSuccess = {}, onNavigateToForgotPassword = { })
}