package com.example.kalanacommerce.di

import android.util.Log
import com.example.kalanacommerce.BuildConfig
import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.data.remote.service.AuthService
import com.example.kalanacommerce.data.remote.service.AuthServiceImpl
import com.example.kalanacommerce.data.remote.service.ProfileService
import com.example.kalanacommerce.data.remote.service.ProfileServiceImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import okhttp3.Dns
import org.koin.dsl.module
import java.net.Inet4Address
import java.net.InetAddress
import java.util.concurrent.TimeUnit

val networkModule = module {

    single {
        val sessionManager: SessionManager = get()

        HttpClient(OkHttp) {
            expectSuccess = true

            // --- KONFIGURASI MESIN OKHTTP ---
            engine {
                config {
                    retryOnConnectionFailure(true)

                    // Timeout Native OkHttp (Lebih akurat daripada plugin Ktor)
                    connectTimeout(30, TimeUnit.SECONDS)
                    readTimeout(30, TimeUnit.SECONDS)
                    writeTimeout(30, TimeUnit.SECONDS)

                    // 🛠️ FIX MAGIC: PAKSA IPv4
                    // Ini memperbaiki masalah di HP yang IPv6-nya tidak stabil/diblokir provider
                    dns(object : Dns {
                        override fun lookup(hostname: String): List<InetAddress> {
                            return try {
                                // Ambil semua alamat IP
                                val allAddresses = Dns.SYSTEM.lookup(hostname)
                                // Hanya ambil yang IPv4 (Angka biasa, bukan Hexadecimal panjang)
                                val ipv4Addresses = allAddresses.filter { it is Inet4Address }

                                // Kalau ada IPv4, pakai itu. Kalau tidak ada, terpaksa pakai apa aja.
                                if (ipv4Addresses.isNotEmpty()) ipv4Addresses else allAddresses
                            } catch (e: Exception) {
                                // Fallback jika DNS gagal total
                                Dns.SYSTEM.lookup(hostname)
                            }
                        }
                    })
                }
            }

            // Plugin Timeout Ktor (Layer kedua, sekedar safety net)
            install(HttpTimeout) {
                requestTimeoutMillis = 30000L
                connectTimeoutMillis = 30000L
                socketTimeoutMillis = 30000L
            }

            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("KtorNetwork", message)
                    }
                }
            }

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    explicitNulls = false
                    ignoreUnknownKeys = true
                    useAlternativeNames = true
                })
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val token = sessionManager.tokenFlow.firstOrNull()
                        if (!token.isNullOrEmpty()) {
                            BearerTokens(token, "")
                        } else {
                            null
                        }
                    }
                    sendWithoutRequest { request ->
                        val path = request.url.pathSegments
                        !path.contains("login") && !path.contains("register")
                    }
                }
            }

            defaultRequest {
                val rawUrl = BuildConfig.API_BASE_URL
                val safeUrl = if (rawUrl.endsWith("/")) rawUrl else "$rawUrl/"
                url(safeUrl)
            }
        }
    }

    single<AuthService> { AuthServiceImpl(get()) }
    single<ProfileService> { ProfileServiceImpl(get()) }
}