package com.example.kalanacommerce.presentation.screen.dashboard.detail.payment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import org.koin.androidx.compose.koinViewModel

// Warna Brand (Sesuaikan jika sudah ada di Theme)
val BrandGreen = Color(0xFF43A047)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled") // JavaScript dibutuhkan oleh Midtrans Snap
@Composable
fun PaymentScreen(
    paymentUrl: String,
    orderId: String, // ID Order yang sedang dibayar
    viewModel: PaymentViewModel = koinViewModel(),
    onPaymentFinished: (String) -> Unit, // Callback saat selesai/ditutup, membawa Order ID
    onBackClick: () -> Unit // Callback tombol back biasa (jika diperlukan)
) {
    val uiState by viewModel.uiState.collectAsState()

    // [FIX BLANK SCREEN] Flag untuk mencegah double-navigation
    var hasNavigated by remember { mutableStateOf(false) }

    // Wrapper function agar aman
    val safeOnPaymentFinished = { id: String ->
        if (!hasNavigated) {
            hasNavigated = true
            onPaymentFinished(id)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Pembayaran",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    // Tombol Close (X)
                    IconButton(onClick = {
                        // Jika user menutup manual, kita anggap proses selesai
                        // dan arahkan ke Detail Order untuk cek status terbaru.
                        onPaymentFinished(orderId)
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // WebView Native Android
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        // Konfigurasi WebView
                        settings.javaScriptEnabled = true // Wajib untuk Midtrans Snap
                        settings.domStorageEnabled = true // Disarankan

                        webViewClient = object : WebViewClient() {
                            // Saat halaman mulai dimuat
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                viewModel.onPageStarted()
                            }

                            // Saat halaman selesai dimuat
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                viewModel.onPageFinished()
                            }

                            // Handle error (opsional)
                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                super.onReceivedError(view, request, error)
                                viewModel.onError("Gagal memuat halaman: ${error?.description}")
                            }

                            // INTI LOGIKA: Intercept URL Redirect
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                val url = request?.url.toString()

                                // 1. Cek URL indikasi Selesai/Gagal dari Midtrans
                                if (isFinishUrl(url)) {
                                    // Jangan load URL ini di WebView.
                                    // Panggil callback selesai untuk navigasi di App.
                                    safeOnPaymentFinished(orderId)
                                    onPaymentFinished(orderId)
                                    return true // Kita handle sendiri
                                }

                                // 2. Handle Deep Links (misal: gojek://, shopeepay://) untuk buka aplikasi lain
                                if (isDeepLink(url)) {
                                    return try {
                                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url))
                                        view?.context?.startActivity(intent)
                                        true // Kita handle, buka app lain
                                    } catch (e: Exception) {
                                        false // Gagal buka app, biarkan WebView mencoba handle
                                    }
                                }

                                // URL lain biarkan WebView yang handle normal
                                return super.shouldOverrideUrlLoading(view, request)
                            }
                        }
                        // Muat URL pembayaran
                        loadUrl(paymentUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Loading Indicator (Progress Bar) di bagian atas
            if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = BrandGreen
                )
            }
        }
    }
}

// --- Helper Functions ---

// Mendeteksi URL indikasi selesai/gagal dari Midtrans.
// Sesuaikan pola string ini dengan konfigurasi callback URL Anda di dashboard Midtrans.
private fun isFinishUrl(url: String): Boolean {
    return url.contains("/finish") ||         // Pola umum sukses
            url.contains("/success") ||       // Pola lain sukses
            url.contains("/error") ||         // Pola gagal
            url.contains("/unfinish") ||      // Pola belum selesai
            url.contains("transaction_status=") // Parameter status dari Midtrans
}

// Mendeteksi Deep Link (skema non-http/https)
private fun isDeepLink(url: String): Boolean {
    return !url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("about:blank")
}