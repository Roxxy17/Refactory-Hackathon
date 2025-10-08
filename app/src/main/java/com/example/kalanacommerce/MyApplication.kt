package com.example.kalanacommerce

import android.app.Application
import com.example.kalanacommerce.di.appModule
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule) // Daftarkan modul Koin Anda
        }
    }
}