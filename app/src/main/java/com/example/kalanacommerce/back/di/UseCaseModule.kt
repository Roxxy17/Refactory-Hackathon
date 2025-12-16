package com.example.kalanacommerce.back.di
import com.example.kalanacommerce.back.domain.usecase.auth.LogoutUseCase
import com.example.kalanacommerce.back.domain.usecase.auth.RegisterUseCase
import com.example.kalanacommerce.back.domain.usecase.auth.SignInUseCase
import org.koin.dsl.module

val useCaseModule = module {
    // Auth
    factory { SignInUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { LogoutUseCase(get()) }
}
