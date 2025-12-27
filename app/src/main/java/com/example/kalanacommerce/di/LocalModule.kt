package com.example.kalanacommerce.di

import com.example.kalanacommerce.data.local.datastore.SessionManager
import com.example.kalanacommerce.data.local.datastore.ThemeManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localModule = module {
    single { SessionManager(androidContext()) }

    single { ThemeManager(androidContext())}
}