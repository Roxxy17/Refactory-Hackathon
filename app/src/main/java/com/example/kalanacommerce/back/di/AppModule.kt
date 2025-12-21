package com.example.kalanacommerce.back.di

import com.example.kalanacommerce.back.data.local.datastore.ThemeManager // Pastikan di-import
import com.example.kalanacommerce.front.screen.auth.register.RegisterViewModel
import com.example.kalanacommerce.front.screen.auth.login.SignInViewModel
import com.example.kalanacommerce.back.util.DefaultDispatcherProvider
import com.example.kalanacommerce.back.util.DispatcherProvider
import com.example.kalanacommerce.front.screen.dashboard.profile.ProfileViewModel
import org.koin.android.ext.koin.androidContext // Import untuk androidContext()
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<DispatcherProvider> { DefaultDispatcherProvider() }

    // --- 1. DAFTARKAN ThemeManager DI SINI ---
    // (Wajib ada agar bisa di-inject ke ViewModel dan Activity)
    single { ThemeManager(androidContext()) }

    viewModel {
        SignInViewModel(
            signInUseCase = get(),
            sessionManager = get()
        )
    }

    viewModel {
        RegisterViewModel(
            registerUseCase = get()
        )
    }

    // --- 2. UPDATE ProfileViewModel DI SINI ---
    // Sekarang membutuhkan 2 parameter: SessionManager & ThemeManager
    viewModel {
        ProfileViewModel(
            sessionManager = get(), // get() pertama mengambil SessionManager
            themeManager = get()    // get() kedua mengambil ThemeManager
        )
    }
}