package com.example.kalanacommerce.di

import com.example.kalanacommerce.R
import com.example.kalanacommerce.data.local.datastore.ThemeManager // Pastikan di-import
import com.example.kalanacommerce.presentation.screen.auth.register.RegisterViewModel
import com.example.kalanacommerce.presentation.screen.auth.login.SignInViewModel
import com.example.kalanacommerce.core.util.DefaultDispatcherProvider
import com.example.kalanacommerce.core.util.DispatcherProvider
import com.example.kalanacommerce.data.local.datastore.LanguageManager
import com.example.kalanacommerce.presentation.screen.auth.forgotpassword.ForgotPasswordViewModel
import com.example.kalanacommerce.presentation.screen.dashboard.cart.CartViewModel
import com.example.kalanacommerce.presentation.screen.dashboard.chat.ChatViewModel
import com.example.kalanacommerce.presentation.screen.dashboard.detail.checkout.CheckoutViewModel
import com.example.kalanacommerce.presentation.screen.dashboard.detail.payment.PaymentViewModel
import com.example.kalanacommerce.presentation.screen.dashboard.explore.ExploreViewModel
import com.example.kalanacommerce.presentation.screen.dashboard.history.OrderHistoryViewModel
import com.example.kalanacommerce.presentation.screen.dashboard.history.detail.DetailOrderViewModel
import com.example.kalanacommerce.presentation.screen.dashboard.home.HomeViewModel
import com.example.kalanacommerce.presentation.screen.dashboard.detail.product.DetailProductViewModel
import com.example.kalanacommerce.presentation.screen.dashboard.profile.ProfileViewModel
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.addresspage.AddressViewModel
import com.example.kalanacommerce.presentation.screen.dashboard.profile.subscreen.profilepage.EditProfileViewModel
import com.example.kalanacommerce.presentation.screen.dashboard.detail.store.DetailStoreViewModel
import com.example.kalanacommerce.presentation.screen.dashboard.detail.success.OrderSuccessViewModel
import com.example.kalanacommerce.presentation.screen.dashboard.history.group.TransactionGroupViewModel
import org.koin.android.ext.koin.androidContext // Import untuk androidContext()
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single { ThemeManager(androidContext()) }
    single { LanguageManager(androidContext()) }

    viewModel { AddressViewModel(get()) }

    viewModel {
        SignInViewModel(
            get(), get()
        )
    }

    viewModel {
        RegisterViewModel(
            get()
        )
    }

    viewModel {
        ProfileViewModel(
            get(), get(), get(), get(), androidContext()
        )
    }
    // ForgotPasswordViewModel
    viewModel {
        ForgotPasswordViewModel(
            get(), get()
        )
    }

    viewModel {
        EditProfileViewModel(
            get(), get()
        )
    }

    viewModel {
        HomeViewModel(
            get(), get(), get(), get(), androidContext()
        )
    }

    viewModel {
        ChatViewModel(
            get(),
            androidContext().getString(R.string.chat_welcome_message),
        )
    }

    viewModel {
        ExploreViewModel(
            get(), get() // [TAMBAHKAN INI]
        )
    }

    viewModel {
        DetailProductViewModel(
            get(), get()
        )
    }
    viewModel { DetailStoreViewModel(get()) }
    viewModel { OrderHistoryViewModel(get()) }
    viewModel { DetailOrderViewModel(get(), get()) }

    viewModel {
        CartViewModel(
            get(), get(), get()
        )
    }

    viewModel {
        CheckoutViewModel(
            get(), get(), get(), get(), get(), get()
        )
    }

    viewModel { PaymentViewModel() }

    viewModel { TransactionGroupViewModel(get()) }

    viewModel {
        OrderSuccessViewModel(
            get(), get(), get()
        )
    }
}
