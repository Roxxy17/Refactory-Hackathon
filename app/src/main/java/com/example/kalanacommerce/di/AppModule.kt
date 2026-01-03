package com.example.kalanacommerce.di

import com.example.kalanacommerce.data.local.datastore.ThemeManager // Pastikan di-import
import com.example.kalanacommerce.presentation.screen.auth.register.RegisterViewModel
import com.example.kalanacommerce.presentation.screen.auth.login.SignInViewModel
import com.example.kalanacommerce.core.util.DefaultDispatcherProvider
import com.example.kalanacommerce.core.util.DispatcherProvider
import com.example.kalanacommerce.data.local.datastore.LanguageManager
import com.example.kalanacommerce.data.remote.service.AddressService
import com.example.kalanacommerce.data.remote.service.AddressServiceImpl
import com.example.kalanacommerce.data.repository.AddressRepositoryImpl
import com.example.kalanacommerce.domain.repository.AddressRepository
import com.example.kalanacommerce.presentation.screen.auth.forgotpassword.ForgotPasswordViewModel
import com.example.kalanacommerce.presentation.screen.dashboard.profile.ProfileViewModel
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.addresspage.AddressViewModel
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.profilepage.EditProfileViewModel
import org.koin.android.ext.koin.androidContext // Import untuk androidContext()
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<DispatcherProvider> { DefaultDispatcherProvider() }

    // --- 1. DAFTARKAN ThemeManager DI SINI ---
    // (Wajib ada agar bisa di-inject ke ViewModel dan Activity)
    single { ThemeManager(androidContext()) }
    single { LanguageManager(androidContext()) }

    single<AddressService> { AddressServiceImpl(get()) }
    single<AddressRepository> { AddressRepositoryImpl(get()) }
    viewModel { AddressViewModel(get()) }

    viewModel {
        SignInViewModel(
            signInUseCase = get(), sessionManager = get()
        )
    }

    viewModel {
        RegisterViewModel(
            registerUseCase = get()
        )
    }

    // --- PERBAIKAN DI SINI ---
    viewModel {
        ProfileViewModel(
            sessionManager = get(),
            themeManager = get(),
            languageManager = get(),
            profileRepository = get(), // <-- JANGAN LUPA INI
            context = androidContext() // Gunakan androidContext() dari Koin
        )
    }
    // ForgotPasswordViewModel
    viewModel {
        ForgotPasswordViewModel(
            forgotPasswordUseCase = get(),
            resetPasswordUseCase = get()
        )
    }

    viewModel {
        EditProfileViewModel(
            profileRepository = get(), // Inject ProfileRepository
            sessionManager = get()     // Inject SessionManager (PENTING untuk update data lokal)
        )
    }
}