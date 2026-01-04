package com.example.kalanacommerce.di
import com.example.kalanacommerce.domain.usecase.auth.LogoutUseCase
import com.example.kalanacommerce.domain.usecase.auth.RegisterUseCase
import com.example.kalanacommerce.domain.usecase.auth.SignInUseCase
import com.example.kalanacommerce.domain.usecase.auth.forgot.ForgotPasswordUseCase
import com.example.kalanacommerce.domain.usecase.auth.forgot.ResetPasswordUseCase
import com.example.kalanacommerce.domain.usecase.profile.GetProfileUseCase
import com.example.kalanacommerce.domain.usecase.profile.UpdatePhotoUseCase
import com.example.kalanacommerce.domain.usecase.profile.UpdateProfileUseCase
import org.koin.dsl.module

val useCaseModule = module {
    // Auth
    factory { SignInUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { LogoutUseCase(get()) }
    single { ForgotPasswordUseCase(get()) }
    single { ResetPasswordUseCase(get()) }
    factory { GetProfileUseCase(get()) }
    factory { UpdateProfileUseCase(get()) }
    factory { UpdatePhotoUseCase(get()) }
}
