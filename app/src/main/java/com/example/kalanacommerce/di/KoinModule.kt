package com.example.kalanacommerce.di

import com.example.kalanacommerce.data.AuthService
import com.example.kalanacommerce.data.AuthServiceImpl
import com.example.kalanacommerce.ui.viewmodel.RegisterViewModel
import com.example.kalanacommerce.ui.viewmodel.SignInViewModel
import io.ktor.client.*
import io.ktor.client.engine.android.* // Menggunakan Android Engine
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.net.ssl.HostnameVerifier
import io.ktor.client.plugins.logging.* // Wajib diimpor!
import io.ktor.client.plugins.cookies.* // Wajib diimpor
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage // <---

val appModule = module {
    // Ktor HttpClient
    single {
        HttpClient(Android) { // Gunakan Android engine untuk performa terbaik di Android
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true // Penting jika API responsnya ada field yang tidak diserialisasi
                    prettyPrint = true
                    isLenient = true
                })
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL // Tampilkan semua header dan body
            }


            // Konfigurasi JSON
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            install(HttpCookies) {
                storage = AcceptAllCookiesStorage()
            }
            engine {
                // Konfigurasi ini hanya berfungsi jika Anda menggunakan Android engine
                // Menonaktifkan pemeriksaan SSL, sehingga sertifikat kustom akan diterima
                sslManager = { httpsURLConnection ->
                    httpsURLConnection.hostnameVerifier = HostnameVerifier { _, _ -> true }
                    httpsURLConnection.sslSocketFactory = createBadSslSocketFactory()
                }
            }
            // Tambahkan Logging atau Timeout jika perlu
        }
    }

    // AuthService
    single<AuthService> {
        AuthServiceImpl(client = get())
    }

    // SignInViewModel
    viewModel {
        SignInViewModel(authService = get())
    }

    // 4. RegisterViewModel (BARU)
    viewModel {
        RegisterViewModel(authService = get())
    }
}

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
