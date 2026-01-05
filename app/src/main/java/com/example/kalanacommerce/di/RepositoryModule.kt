package com.example.kalanacommerce.di

import com.example.kalanacommerce.data.remote.service.AddressService
import com.example.kalanacommerce.data.remote.service.AddressServiceImpl
import com.example.kalanacommerce.data.remote.service.ProductApiService
import com.example.kalanacommerce.data.remote.service.ProductApiServiceImpl
import com.example.kalanacommerce.data.repository.AddressRepositoryImpl
import com.example.kalanacommerce.data.repository.AuthRepositoryImpl
import com.example.kalanacommerce.data.repository.ProductRepositoryImpl
import com.example.kalanacommerce.domain.repository.AuthRepository
import com.example.kalanacommerce.data.repository.ProfileRepositoryImpl
import com.example.kalanacommerce.domain.repository.AddressRepository
import com.example.kalanacommerce.domain.repository.ProductRepository
import com.example.kalanacommerce.domain.repository.ProfileRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<AuthRepository> {
        AuthRepositoryImpl(
            authService = get(), sessionManager = get()
        )
    }

    single<ProfileRepository> {
        ProfileRepositoryImpl(
            profileService = get()
        )
    }
    single<AddressService> { AddressServiceImpl(get()) }
    single<AddressRepository> { AddressRepositoryImpl(get()) }


        // 1. API Service
    single<ProductApiService> { ProductApiServiceImpl(get()) }

    // Repository tetap sama, dia minta ProductApiService (Interface)
    single<ProductRepository> { ProductRepositoryImpl(get()) }
}
