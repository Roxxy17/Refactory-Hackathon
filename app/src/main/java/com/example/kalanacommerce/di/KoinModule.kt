package com.example.kalanacommerce.di

import com.example.kalanacommerce.data.AuthRepository
import com.example.kalanacommerce.data.AuthRepositoryImpl
import com.example.kalanacommerce.data.AuthService
import com.example.kalanacommerce.data.AuthServiceImpl
import com.example.kalanacommerce.data.TokenManager
import com.example.kalanacommerce.data.local.SessionManager
import com.example.kalanacommerce.data.repository.OrderRepository
import com.example.kalanacommerce.data.repository.OrderRepositoryImpl
import com.example.kalanacommerce.data.repository.ProductRepository
import com.example.kalanacommerce.data.repository.ProductRepositoryImpl
import com.example.kalanacommerce.data.service.ChatService
import com.example.kalanacommerce.data.service.ChatServiceImpl
import com.example.kalanacommerce.ui.viewmodel.AuthViewModel
import com.example.kalanacommerce.ui.viewmodel.ChatViewModel
import com.example.kalanacommerce.ui.viewmodel.OrderViewModel
import com.example.kalanacommerce.ui.viewmodel.ProductViewModel
import com.example.kalanacommerce.ui.viewmodel.RegisterViewModel
import com.example.kalanacommerce.ui.viewmodel.SignInViewModel
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Modul untuk Ketergantungan Jaringan (Ktor)
 */
val networkModule = module {
    single {
        TokenManager(androidContext())
    }
    single<OrderRepository> {
        OrderRepositoryImpl(get()) // Membutuhkan HttpClient
    }
    single<ProductRepository> {
        ProductRepositoryImpl(client = get()) // ProductRepositoryImpl membutuhkan HttpClient (get())
    }
    single<ChatService> { ChatServiceImpl(client = get()) }
    single {

        val tokenManager: TokenManager = get()
        HttpClient(Android) {
            // Install ContentNegotiation untuk parsing JSON (HANYA SEKALI)
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                    explicitNulls = false
                })
            }

            // Install Logging untuk debugging
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }

            // Install Cookies jika diperlukan oleh API Anda
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
            }
            defaultRequest {
                val token = tokenManager.getToken() // <-- Membaca dari penyimpanan lokal
                if (token != null) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }

            // Konfigurasi engine untuk menonaktifkan pemeriksaan SSL (HATI-HATI: HANYA UNTUK DEVELOPMENT)
            engine {
                sslManager = { httpsURLConnection ->
                    httpsURLConnection.hostnameVerifier = HostnameVerifier { _, _ -> true }
                    httpsURLConnection.sslSocketFactory = createBadSslSocketFactory()
                }
            }
        }
    }
}

/**
 * Modul untuk Lapisan Service (yang berkomunikasi dengan jaringan)
 */
val serviceModule = module {
    single<AuthService> { AuthServiceImpl(client = get()) }
}

/**
 * Modul untuk Lapisan Repository (jembatan antara data dan ViewModel)
 */
val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
}

/**
 * Modul untuk Ketergantungan Lokal (seperti DataStore)
 */
val localModule = module {
    single { SessionManager(androidContext()) }
}

/**
 * Modul untuk semua ViewModel
 */
val viewModelModule = module {
    // ViewModel baru yang mengelola sesi dan logika otentikasi secara umum
    viewModel { AuthViewModel(get(), get()) }

    // ViewModel lama Anda (jika masih digunakan)
    viewModel { SignInViewModel(authService = get(), tokenManager = get()) }
    viewModel { RegisterViewModel(authService = get()) }

    viewModel {
        // OrderViewModel membutuhkan OrderRepository (get())
        OrderViewModel(repository = get())
    }
    viewModel { ChatViewModel(chatService = get()) }

    // SignInViewModel

    viewModel {
        // ProductViewModel membutuhkan ProductRepository (get())
        ProductViewModel(repository = get())
    }
}


// Fungsi helper untuk membuat SSLSocketFactory yang tidak aman (Hanya untuk Development)
private fun createBadSslSocketFactory(): SSLSocketFactory {
    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
    })

    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, trustAllCerts, java.security.SecureRandom())
    return sslContext.socketFactory
}
