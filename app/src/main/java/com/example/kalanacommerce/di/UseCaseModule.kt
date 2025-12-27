package com.example.kalanacommerce.di
import com.example.kalanacommerce.domain.usecase.auth.LogoutUseCase
import com.example.kalanacommerce.domain.usecase.auth.RegisterUseCase
import com.example.kalanacommerce.domain.usecase.auth.SignInUseCase
import org.koin.dsl.module

val useCaseModule = module {
    // Auth
    factory { SignInUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { LogoutUseCase(get()) }
}
