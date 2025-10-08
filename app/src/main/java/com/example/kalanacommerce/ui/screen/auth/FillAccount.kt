package com.example.kalanacommerce.ui.screen.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.R
import com.example.kalanacommerce.ui.theme.KalanaCommerceTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FillAccountScreen(
    onNavigateBack: () -> Unit,
    onContinue: () -> Unit
) {
    val primaryColor = Color(0xFF069C6F)
    val lightGray = Color(0xFFF1F1F1)
    val darkTextColor = Color(0xFF555555)
    val errorColor = Color(0xFFD32F2F)

    var fullName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = lightGray,
        unfocusedContainerColor = lightGray,
        unfocusedBorderColor = Color.Transparent,
        focusedBorderColor = primaryColor,
        focusedTextColor = darkTextColor,
        unfocusedTextColor = darkTextColor
    )

    Scaffold(
        // PERBAIKAN 1: Buat Scaffold menjadi transparan
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Fill Your Account", color = primaryColor, fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                // PERBAIKAN 2: Buat TopAppBar juga transparan
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, primaryColor.copy(alpha = 0.05f))
                    )
                )
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.size(120.dp).clickable { /* TODO: Aksi untuk memilih gambar */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonOutline,
                    contentDescription = "Profile Picture Placeholder",
                    modifier = Modifier.fillMaxSize().clip(CircleShape).background(lightGray).padding(24.dp),
                    tint = Color.Gray
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile Picture",
                    modifier = Modifier.align(Alignment.BottomEnd).size(36.dp).clip(CircleShape).background(Color.White).border(2.dp, Color.White, CircleShape).padding(6.dp),
                    tint = primaryColor
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(visible = errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = errorColor,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Full Name", color = Color.Gray) },
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                    singleLine = true
                )

                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                    placeholder = { Text("Date of Birth", color = Color.Gray) },
                    trailingIcon = { Icon(Icons.Outlined.CalendarToday, contentDescription = "Calendar") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = lightGray,
                        unfocusedContainerColor = lightGray,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = primaryColor,
                        focusedTextColor = darkTextColor,
                        unfocusedTextColor = darkTextColor,
                        disabledTextColor = darkTextColor
                    ),
                    readOnly = true
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Phone Number", color = Color.Gray) },
                    leadingIcon = { CountryCodePicker() },
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Alamat", color = Color.Gray) },
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                    minLines = 3
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (isLoading) {
                CircularProgressIndicator(color = primaryColor, modifier = Modifier.padding(bottom = 16.dp))
            }

            Button(
                onClick = { /* TODO: Aksi, contoh: isLoading = true */ },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                enabled = !isLoading
            ) {
                Text("Continue", color = Color.White, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            dateOfBirth = formatter.format(Date(millis))
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
private fun CountryCodePicker() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_flag_indonesia),
            contentDescription = "Indonesia Flag",
            modifier = Modifier.size(24.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun FillAccountScreenPreview() {
    KalanaCommerceTheme {
        FillAccountScreen(onNavigateBack = {}, onContinue = {})
    }
}