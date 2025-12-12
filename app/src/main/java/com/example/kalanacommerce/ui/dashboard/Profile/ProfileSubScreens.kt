package com.example.kalanacommerce.ui.dashboard.Profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalanacommerce.ui.theme.KalanaCommerceTheme // Asumsi

// Warna abu terang
private val LightBackground = Color(0xFFF7F7F7)

// =====================================================================
// === 1. EDIT PROFILE PAGE ===
// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfilePage(onBack: () -> Unit) {
    val primaryColor = MaterialTheme.colorScheme.primary
    var name by remember { mutableStateOf("Sinta Dewi") }
    var email by remember { mutableStateOf("sinta.dewi@example.com") }
    var phone by remember { mutableStateOf("081234567890") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil", fontWeight = FontWeight.Bold, color = primaryColor) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = LightBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Placeholder Foto Profil
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.1f))
                    .clickable { /* Aksi Ganti Foto */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Ubah Foto", tint = primaryColor, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Input Fields
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Lengkap") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth(), enabled = false) // Email biasanya tidak bisa diubah
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Nomor Telepon") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Simpan
            Button(
                onClick = { /* Aksi Simpan Profil */ onBack() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text("Simpan Perubahan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// =====================================================================
// === 2. ADDRESS PAGE ===
// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressPage(onBack: () -> Unit) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val addresses = remember {
        listOf(
            "Rumah: Jl. Sudirman No. 12, Jakarta Pusat, DKI Jakarta",
            "Kantor: Gedung A Lt. 5, Jl. Rasuna Said, Kuningan, Jakarta Selatan"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alamat Pengiriman", fontWeight = FontWeight.Bold, color = primaryColor) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Aksi Tambah Alamat */ }) {
                        Icon(Icons.Default.AddLocation, contentDescription = "Tambah Alamat", tint = primaryColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = LightBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            addresses.forEachIndexed { index, address ->
                AddressCard(
                    title = if (index == 0) "Alamat Utama" else "Alamat Lain",
                    address = address,
                    onEdit = { /* Aksi Edit */ },
                    isPrimary = index == 0
                )
            }
        }
    }
}

@Composable
fun AddressCard(title: String, address: String, onEdit: () -> Unit, isPrimary: Boolean) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = primaryColor
                )
                if (isPrimary) {
                    Text("UTAMA", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(primaryColor, CircleShape)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = address, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onEdit,
                shape = RoundedCornerShape(8.dp),
                // Perbaikan: Gunakan BorderStroke untuk mendefinisikan border.
                // Parameter `border` sekarang menerima `BorderStroke`.
                // `ButtonDefaults.outlinedButtonBorder` tidak lagi digunakan seperti ini.
                border = BorderStroke(1.dp, primaryColor),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor)
            ) {
                Text("Ubah Alamat")
            }
        }
    }
}

// =====================================================================
// === 3. SETTINGS PAGE ===
// =====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(onBack: () -> Unit) {
    val primaryColor = MaterialTheme.colorScheme.primary
    var isNotificationEnabled by remember { mutableStateOf(true) }
    var isDarkModeEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan Aplikasi", fontWeight = FontWeight.Bold, color = primaryColor) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = LightBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsToggleItem(
                title = "Notifikasi Promosi",
                subtitle = "Dapatkan pemberitahuan tentang diskon terbaru.",
                checked = isNotificationEnabled,
                onCheckedChange = { isNotificationEnabled = it }
            )
            SettingsToggleItem(
                title = "Mode Gelap",
                subtitle = "Aktifkan tema gelap untuk kenyamanan mata.",
                checked = isDarkModeEnabled,
                onCheckedChange = { isDarkModeEnabled = it }
            )
            SettingsLinkItem(
                title = "Kebijakan Privasi",
                onClick = { /* Navigasi ke halaman web */ }
            )
            SettingsLinkItem(
                title = "Tentang Aplikasi",
                onClick = { /* Navigasi ke halaman About */ }
            )
        }
    }
}

@Composable
fun SettingsToggleItem(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary))
    }
}

@Composable
fun SettingsLinkItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, fontWeight = FontWeight.SemiBold)
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
    }
}


// --- PREVIEW KOMBINASI ---

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun ProfileSubScreensPreview() {
    KalanaCommerceTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            EditProfilePage(onBack = {})
            // AddressPage(onBack = {}) // Komponen tunggal tidak dapat dimasukkan di sini
            // SettingsPage(onBack = {})
        }
    }
}