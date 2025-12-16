package com.example.kalanacommerce.back.di

import com.example.kalanacommerce.back.data.repository.AuthRepositoryImpl
import com.example.kalanacommerce.back.domain.repository.AuthRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<AuthRepository> {
        AuthRepositoryImpl(
            authService = get(),
            sessionManager = get()
        )
    }

}
