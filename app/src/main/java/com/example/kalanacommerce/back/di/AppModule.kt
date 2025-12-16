package com.example.kalanacommerce.back.di

import com.example.kalanacommerce.front.screen.auth.register.RegisterViewModel
import com.example.kalanacommerce.front.screen.auth.login.SignInViewModel
import com.example.kalanacommerce.back.util.DefaultDispatcherProvider
import com.example.kalanacommerce.back.util.DispatcherProvider
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<DispatcherProvider> { DefaultDispatcherProvider() }

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
}
