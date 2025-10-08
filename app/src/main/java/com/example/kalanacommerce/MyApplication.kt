package com.example.kalanacommerce

import android.app.Application
import com.example.kalanacommerce.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(appModule) // Daftarkan modul Koin Anda
        }
    }
}