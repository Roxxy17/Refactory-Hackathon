package com.example.kalanacommerce.di

import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.data.remote.service.AuthService
import com.example.kalanacommerce.data.remote.service.AuthServiceImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

val networkModule = module {

    single {
        val sessionManager: SessionManager = get()

        HttpClient(Android) {

            // --- PERBAIKAN UTAMA DISINI ---
            // Ini memaksa Ktor melempar Exception jika status code bukan 2xx (200-299).
            // Error 4xx -> ClientRequestException
            // Error 5xx -> ServerResponseException
            expectSuccess = true
            // ------------------------------

            install(Logging) {
                level = LogLevel.ALL
                logger = Logger.DEFAULT // Digabung agar lebih rapi
            }

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    explicitNulls = false
                    ignoreUnknownKeys = true
                })
            }

            defaultRequest {
                val token = runBlocking {
                    sessionManager.tokenFlow.firstOrNull()
                }

                if (token != null) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
    }

    single<AuthService> { AuthServiceImpl(get()) }
}

// Helper Function SSL (Biarkan saja jika memang dipakai untuk debug/bypass SSL)
private fun createBadSslSocketFactory(): SSLSocketFactory {
    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
    })

    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, trustAllCerts, SecureRandom())
    return sslContext.socketFactory
}