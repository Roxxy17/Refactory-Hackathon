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
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.http.encodedPath
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

        // 1. BUAT INSTANCE CLIENT
        val client = HttpClient(OkHttp) {
            expectSuccess = true

            engine {
                config {
                    retryOnConnectionFailure(true)
                    connectTimeout(30, TimeUnit.SECONDS)
                    readTimeout(30, TimeUnit.SECONDS)
                    writeTimeout(30, TimeUnit.SECONDS)

                    dns(object : Dns {
                        override fun lookup(hostname: String): List<InetAddress> {
                            return try {
                                val allAddresses = Dns.SYSTEM.lookup(hostname)
                                val ipv4Addresses = allAddresses.filter { it is Inet4Address }
                                if (ipv4Addresses.isNotEmpty()) ipv4Addresses else allAddresses
                            } catch (e: Exception) {
                                Dns.SYSTEM.lookup(hostname)
                            }
                        }
                    })
                }
            }

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

            defaultRequest {
                val rawUrl = BuildConfig.API_BASE_URL
                val safeUrl = if (rawUrl.endsWith("/")) rawUrl else "$rawUrl/"
                url(safeUrl)
            }
        }

        // 2. PASANG INTERCEPTOR (PERBAIKAN SINTAKS DI SINI)
        // Menggunakan 'HttpRequestPipeline.Before' langsung, bukan 'State.Before'
        client.requestPipeline.intercept(HttpRequestPipeline.Before) {
            try {
                val token = sessionManager.tokenFlow.firstOrNull()

                // 'context' di sini adalah HttpRequestBuilder
                val path = context.url.encodedPath
                val isAuthPath = path.contains("auth/login") || path.contains("auth/register")

                if (!token.isNullOrEmpty() && !isAuthPath) {
                    context.headers.append("Authorization", "Bearer $token")
                    Log.d("KtorNetwork", "Token Attached (Async): ${token.takeLast(6)}")
                }
            } catch (e: Exception) {
                Log.e("KtorNetwork", "Gagal attach token: ${e.message}")
            }
        }

        // 3. RETURN CLIENT
        client
    }

    single<AuthService> { AuthServiceImpl(get()) }
    single<ProfileService> { ProfileServiceImpl(get()) }
}