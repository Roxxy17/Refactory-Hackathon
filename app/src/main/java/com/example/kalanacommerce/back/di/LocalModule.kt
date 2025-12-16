package com.example.kalanacommerce.back.di

import com.example.kalanacommerce.back.data.local.datastore.SessionManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localModule = module {
    single { SessionManager(androidContext()) }
}
