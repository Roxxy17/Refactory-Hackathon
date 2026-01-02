package com.example.kalanacommerce.di

import com.example.kalanacommerce.data.repository.AuthRepositoryImpl
import com.example.kalanacommerce.domain.repository.AuthRepository
import com.example.kalanacommerce.data.repository.ProfileRepositoryImpl
import com.example.kalanacommerce.domain.repository.ProfileRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<AuthRepository> {
        AuthRepositoryImpl(
            authService = get(),
            sessionManager = get()
        )
    }

    single<ProfileRepository> {
        ProfileRepositoryImpl(
            profileService = get()
        )
    }

}
