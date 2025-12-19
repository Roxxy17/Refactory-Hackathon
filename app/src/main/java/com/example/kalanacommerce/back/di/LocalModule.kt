package com.example.kalanacommerce.back.di

import com.example.kalanacommerce.back.data.local.datastore.SessionManager
import com.example.kalanacommerce.back.data.local.datastore.ThemeManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localModule = module {
    single { SessionManager(androidContext()) }

    single { ThemeManager(androidContext())}
}