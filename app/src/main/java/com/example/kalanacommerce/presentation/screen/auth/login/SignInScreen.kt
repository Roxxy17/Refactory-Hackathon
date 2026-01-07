package com.example.kalanacommerce.presentation.screen.auth.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
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
import com.example.kalanacommerce.core.util.connectToAutofill
// Pastikan import ini sesuai dengan lokasi file CustomToast.kt Anda
import com.example.kalanacommerce.presentation.components.CustomToast
import com.example.kalanacommerce.presentation.components.ToastType
import com.example.kalanacommerce.presentation.theme.*
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onSignInSuccess: () -> Unit,
    viewModel: SignInViewModel = koinViewModel()
) {

    val blobColor1 = MaterialTheme.colorScheme.primary
    val blobColor2 = MaterialTheme.colorScheme.secondary

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // --- State untuk Custom Toast ---
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var toastType by remember { mutableStateOf(ToastType.Success) }

    // --- State Input ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    val lastSavedEmail by viewModel.lastEmail.collectAsStateWithLifecycle()

    // Di dalam LoginScreen, sebelum Column
    val emailAutofill = remember { listOf(AutofillType.EmailAddress) }
    val passwordAutofill = remember { listOf(AutofillType.Password) }

    val backgroundColor = MaterialTheme.colorScheme.background

    // Validasi sederhana
    val isFormValid = remember(email, password) {
        email.isNotBlank() && password.isNotEmpty()
    }

    // Logic Sukses
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            val name = uiState.userName ?: ""
            // Mengambil string dari strings.xml dengan parameter
            toastMessage = context.getString(R.string.welcome_back_user, name)
            toastType = ToastType.Success
            showToast = true // Trigger animasi muncul

            // Beri jeda sedikit sebelum pindah layar agar toast sempat terlihat
            delay(1500)
            onSignInSuccess()
        }
    }

    // Logic Error
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            toastMessage = uiState.error ?: "Unknown Error"
            toastType = ToastType.Error
            showToast = true
        }
    }

    // 3. EFFECT: Saat lastSavedEmail berhasil dimuat dari DataStore, masukkan ke variable 'email'
    // 'Unit' memastikan ini hanya berjalan saat pertama kali lastSavedEmail berubah dari null ke ada isinya
    LaunchedEffect(lastSavedEmail) {
        if (!lastSavedEmail.isNullOrEmpty()) {
            email = lastSavedEmail ?: ""
            // Opsional: Otomatis centang remember me jika email sudah ada
            rememberMe = true
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "Infinite BG")

    // Animasi posisi/skala background blob 1
    val blob1Scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse),
        label = "Blob1"
    )

    // Animasi posisi background blob 2
    val blob2Offset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 50f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Reverse),
        label = "Blob2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
    ) {
        // --- 4. BACKGROUND DECORATION (Modern Blobs) ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Blob Atas Kiri
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(blobColor1.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(0f, 0f),
                    radius = size.width * 0.8f * blob1Scale
                ),
                center = Offset(0f, 0f),
                radius = size.width * 0.8f * blob1Scale
            )

            // Blob Bawah Kanan
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(blobColor2.copy(alpha = 0.5f), Color.Transparent),
                    center = Offset(size.width, size.height),
                    radius = size.width * 0.9f
                ),
                center = Offset(size.width - blob2Offset, size.height + blob2Offset),
                radius = size.width * 0.9f
            )
        }

        // --- KONTEN UTAMA ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp),
//                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {



            HeaderSection()

            Spacer(modifier = Modifier.height(32.dp))

            LoginTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = stringResource(id = R.string.edit_profile_email),
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
                onDone = { focusManager.clearFocus() },
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        // Opsi tambahan: fillMaxWidth agar area klik memenuhi lebar (opsional)
                        // .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)) // Agar ripple effect saat diklik tidak kotak kaku
                        .clickable { rememberMe = !rememberMe }
                        .padding(vertical = 4.dp) // Sedikit padding agar ripple tidak terpotong
                ) {
                    Checkbox(
                        checked = rememberMe,
                        // UBAH DI SINI: Set null agar klik tembus ke Row
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.outline,
                            // Perlu set checkmarkColor agar kontras jika menggunakan onCheckedChange = null
                            checkmarkColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp)) // Beri sedikit jarak antara box dan text

                    Text(
                        text = stringResource(id = R.string.remember_me),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = stringResource(id = R.string.forgot_password),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.clickable(onClick = onNavigateToForgotPassword)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ERROR MESSAGE TEXT (Opsional: Bisa dihapus jika sudah pakai Toast,
            // tapi dibiarkan juga tidak apa-apa sebagai double feedback)
            AnimatedVisibility(
                visible = uiState.error != null && !showToast, // Sembunyikan jika toast muncul agar tidak duplikat visual
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = uiState.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ... di dalam Column ...

            Button(
                // --- UBAH BAGIAN ONCLICK INI ---
                onClick = {
                    focusManager.clearFocus() // Tambahkan ini (PENTING untuk trigger save password)
                    viewModel.signIn(email, password, rememberMe)
                },
                // -------------------------------

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
                enabled = !uiState.isLoading && isFormValid
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = stringResource(id = R.string.sign_in),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            SocialLoginSection()

            Spacer(modifier = Modifier.height(16.dp))

            FooterSection(onNavigateToRegister)
        }

        // --- CUSTOM TOAST (Overlay) ---
        // Diletakkan di sini (dalam Box, setelah Column) agar muncul di atas layar
        CustomToast(
            message = toastMessage,
            isVisible = showToast,
            type = toastType,
            onDismiss = { showToast = false }
        )
    }
}

@Composable
private fun HeaderSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Logo mungkin perlu filter warna jika transparan, atau biarkan original jika berwarna
        Image(
            painter = painterResource(id = R.drawable.ic_logo_panjang),
            contentDescription = "Kalana Logo",
            modifier = Modifier.width(180.dp)
            // Opsional: Jika logo Anda hitam pekat dan hilang di Dark Mode, tambahkan ini:
            // colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.login_welcome_back),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground // MENGGUNAKAN WARNA TEMA
            )
        )
        Text(
            text = stringResource(R.string.Sign_in_please_sign_in_to_your_account),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, // MENGGUNAKAN WARNA TEMA
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
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
            Text(
                text = stringResource(id = R.string.or_continue_with),
                modifier = Modifier.padding(horizontal = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Google Button
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
private fun FooterSection(onRegisterClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.dont_have_account) + " ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(id = R.string.sign_up),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onRegisterClick() }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
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
    onTogglePassword: (() -> Unit)? = null,
    autofillTypes: List<AutofillType> = emptyList()
) {
    val containerColor = MaterialTheme.colorScheme.surfaceVariant // Warna isian TextField (Abu muda di Light, Abu tua di Dark)
    val contentColor = MaterialTheme.colorScheme.onSurface

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .connectToAutofill(
                autofillTypes = autofillTypes,
                onFill = { onValueChange(it) } // Saat user pilih saran, update text
            ),
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

@Preview(showBackground = true, heightDp = 800)
@Composable
fun LoginScreenPreview() {
    KalanaCommerceTheme(darkTheme = false) { // Coba ganti true/false
        LoginScreen(
            onNavigateToRegister = {},
            onNavigateToForgotPassword = {},
            onSignInSuccess = {}
        )
    }
}