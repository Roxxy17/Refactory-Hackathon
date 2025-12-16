package com.example.kalanacommerce

import android.app.Application
import com.example.kalanacommerce.back.di.appModule
import com.example.kalanacommerce.back.di.localModule
import com.example.kalanacommerce.back.di.networkModule
import com.example.kalanacommerce.back.di.repositoryModule
import com.example.kalanacommerce.back.di.useCaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {

        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            // * Muat semua modul yang sudah dirapikan
            modules(
                // * bagian di ( back/di)
                networkModule,
                repositoryModule,
                useCaseModule,
                localModule,   // (Nama baru dari DataStoreModule)
                appModule
                // * yang lain jika ada
            )
        }
    }
}
