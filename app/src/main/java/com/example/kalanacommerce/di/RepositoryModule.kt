package com.example.kalanacommerce.di

import com.example.kalanacommerce.data.remote.service.AddressService
import com.example.kalanacommerce.data.remote.service.AddressServiceImpl
import com.example.kalanacommerce.data.remote.service.CartApiService
import com.example.kalanacommerce.data.remote.service.CartApiServiceImpl
import com.example.kalanacommerce.data.remote.service.OrderApiService
import com.example.kalanacommerce.data.remote.service.OrderApiServiceImpl
import com.example.kalanacommerce.data.remote.service.ProductApiService
import com.example.kalanacommerce.data.remote.service.ProductApiServiceImpl
import com.example.kalanacommerce.data.repository.AddressRepositoryImpl
import com.example.kalanacommerce.data.repository.AuthRepositoryImpl
import com.example.kalanacommerce.data.repository.CartRepositoryImpl
import com.example.kalanacommerce.data.repository.ChatRepositoryImpl
import com.example.kalanacommerce.data.repository.OrderRepositoryImpl
import com.example.kalanacommerce.data.repository.ProductRepositoryImpl
import com.example.kalanacommerce.domain.repository.AuthRepository
import com.example.kalanacommerce.data.repository.ProfileRepositoryImpl
import com.example.kalanacommerce.domain.repository.AddressRepository
import com.example.kalanacommerce.domain.repository.CartRepository
import com.example.kalanacommerce.domain.repository.ChatRepository
import com.example.kalanacommerce.domain.repository.OrderRepository
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
    single<ProductApiService> { ProductApiServiceImpl(get()) }
    single<ProductRepository> { ProductRepositoryImpl(get()) }
    single<ChatRepository> { ChatRepositoryImpl(get()) }
    single<CartApiService> { CartApiServiceImpl(get()) }
    single<CartRepository> { CartRepositoryImpl(get()) }
    single<OrderApiService> { OrderApiServiceImpl(get()) }
    single<OrderRepository> { OrderRepositoryImpl(get()) }

}
