package com.example.kalanacommerce.di

import android.util.Log
import com.example.kalanacommerce.BuildConfig
import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.data.remote.dto.refreshtoken.RefreshTokenResponseDto
import com.example.kalanacommerce.data.remote.service.AuthService
import com.example.kalanacommerce.data.remote.service.AuthServiceImpl
import com.example.kalanacommerce.data.remote.service.ChatApiService
import com.example.kalanacommerce.data.remote.service.ChatApiServiceImpl
import com.example.kalanacommerce.data.remote.service.ProfileService
import com.example.kalanacommerce.data.remote.service.ProfileServiceImpl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Dns
import org.koin.dsl.module
import retrofit2.Retrofit
import java.net.Inet4Address
import java.net.InetAddress
import java.util.concurrent.TimeUnit

val networkModule = module {

    single {
        val sessionManager: SessionManager = get()

        // 1. BUAT INSTANCE CLIENT UTAMA
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

            // Install HttpSend agar bisa kita intercept responnya nanti
            install(HttpSend)

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

        // 2. INTERCEPTOR MANUAL (YANG SUDAH JALAN) - TIDAK DIUBAH
        // Ini memastikan token Access tertempel di setiap request awal.
        client.requestPipeline.intercept(HttpRequestPipeline.Before) {
            try {
                val token = sessionManager.tokenFlow.firstOrNull()
                val path = context.url.encodedPath
                val isAuthPath = path.contains("auth/login") || path.contains("auth/register")

                if (!token.isNullOrEmpty() && !isAuthPath) {
                    context.headers.append("Authorization", "Bearer $token")
                    Log.d("KtorNetwork", "Token Attached: ${token.takeLast(6)}")
                }
            } catch (e: Exception) {
                Log.e("KtorNetwork", "Gagal attach token: ${e.message}")
            }
        }

        // 3. [BARU] INTERCEPTOR UNTUK REFRESH TOKEN (HttpSend)
        // Logika: Kalau request gagal 401, kita pause, refresh, lalu ulang requestnya.
        client.plugin(HttpSend).intercept { request ->
            // Jalankan request asli
            val originalCall = execute(request)

            // Cek apakah hasilnya 401 Unauthorized?
            if (originalCall.response.status.value == 401) {
                Log.d("KtorRefresh", "Terdeteksi 401. Mencoba Refresh Token...")

                // Ambil refresh token dari session
                val refreshToken = sessionManager.refreshTokenFlow.firstOrNull()

                if (!refreshToken.isNullOrEmpty()) {
                    try {
                        // Buat client terpisah KHUSUS untuk refresh (biar tidak looping)
                        val refreshClient = HttpClient(OkHttp) {
                            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
                        }

                        // Panggil API Refresh
                        val refreshResponse = refreshClient.post(BuildConfig.API_BASE_URL + "auth/refresh") {
                            contentType(ContentType.Application.Json)
                            setBody(mapOf("refreshToken" to refreshToken))
                        }

                        if (refreshResponse.status.value == 200) {
                            // Sukses Refresh!
                            val newTokenData = refreshResponse.body<RefreshTokenResponseDto>()

                            Log.d("KtorRefresh", "Refresh Berhasil! Token baru: ${newTokenData.data.accessToken.takeLast(5)}")

                            // 1. Simpan Token Baru
                            sessionManager.saveAuthData(
                                newTokenData.data.accessToken,
                                newTokenData.data.refreshToken
                            )

                            // 2. Tempel Token Baru ke Request Lama
                            request.headers[HttpHeaders.Authorization] = "Bearer ${newTokenData.data.accessToken}"

                            // 3. Ulangi Request (Retry)
                            execute(request)
                        } else {
                            Log.e("KtorRefresh", "Gagal Refresh (Bukan 200). Logout.")
                            sessionManager.clearAuthData()
                            originalCall
                        }
                    } catch (e: Exception) {
                        Log.e("KtorRefresh", "Exception saat Refresh: ${e.message}")
                        originalCall
                    }
                } else {
                    Log.e("KtorRefresh", "Tidak ada Refresh Token. Logout.")
                    sessionManager.clearAuthData()
                    originalCall
                }
            } else {
                // Jika bukan 401, kembalikan hasil asli
                originalCall
            }
        }

        // 4. RETURN CLIENT
        client
    }

    single<AuthService> { AuthServiceImpl(get()) }
    single<ProfileService> { ProfileServiceImpl(get()) }
    single<ChatApiService> { ChatApiServiceImpl(get()) }
}

// --- DTO KHUSUS UNTUK REFRESH TOKEN ---
// Tambahkan ini di file yang sama (paling bawah) atau file terpisah


