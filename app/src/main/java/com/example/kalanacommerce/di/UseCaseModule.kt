package com.example.kalanacommerce.di
import com.example.kalanacommerce.domain.usecase.auth.LogoutUseCase
import com.example.kalanacommerce.domain.usecase.auth.RegisterUseCase
import com.example.kalanacommerce.domain.usecase.auth.SignInUseCase
import com.example.kalanacommerce.domain.usecase.auth.forgot.ForgotPasswordUseCase
import com.example.kalanacommerce.domain.usecase.auth.forgot.ResetPasswordUseCase
import org.koin.dsl.module

val useCaseModule = module {
    // Auth
    factory { SignInUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { LogoutUseCase(get()) }
    single { ForgotPasswordUseCase(get()) }
    single { ResetPasswordUseCase(get()) }
}
