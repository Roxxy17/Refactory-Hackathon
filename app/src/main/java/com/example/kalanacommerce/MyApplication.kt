package com.example.kalanacommerce

import android.app.Application
import com.example.kalanacommerce.di.localModule
import com.example.kalanacommerce.di.networkModule
import com.example.kalanacommerce.di.repositoryModule
import com.example.kalanacommerce.di.serviceModule
import com.example.kalanacommerce.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {

        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            // Muat semua modul yang sudah dirapikan
            modules(
                networkModule,
                serviceModule,
                repositoryModule,
                localModule,
                viewModelModule
            )
        }
    }
}