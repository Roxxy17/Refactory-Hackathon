package com.example.kalanacommerce.di
import com.example.kalanacommerce.domain.usecase.auth.LogoutUseCase
import com.example.kalanacommerce.domain.usecase.auth.RegisterUseCase
import com.example.kalanacommerce.domain.usecase.auth.SignInUseCase
import com.example.kalanacommerce.domain.usecase.auth.forgot.ForgotPasswordUseCase
import com.example.kalanacommerce.domain.usecase.auth.forgot.ResetPasswordUseCase
import com.example.kalanacommerce.domain.usecase.cart.AddToCartUseCase
import com.example.kalanacommerce.domain.usecase.cart.CheckoutUseCase
import com.example.kalanacommerce.domain.usecase.cart.DeleteCartItemUseCase
import com.example.kalanacommerce.domain.usecase.cart.DirectCheckoutUseCase
import com.example.kalanacommerce.domain.usecase.cart.GetCartItemsUseCase
import com.example.kalanacommerce.domain.usecase.cart.UpdateCartItemUseCase
import com.example.kalanacommerce.domain.usecase.chat.SendMessageUseCase
import com.example.kalanacommerce.domain.usecase.order.GetOrderDetailUseCase
import com.example.kalanacommerce.domain.usecase.order.GetOrdersUseCase
import com.example.kalanacommerce.domain.usecase.order.UpdatePickupStatusUseCase
import com.example.kalanacommerce.domain.usecase.product.GetCategoriesUseCase
import com.example.kalanacommerce.domain.usecase.product.GetCategoryDetailUseCase
import com.example.kalanacommerce.domain.usecase.product.GetOutletDetailUseCase
import com.example.kalanacommerce.domain.usecase.product.GetOutletsUseCase
import com.example.kalanacommerce.domain.usecase.product.GetProductDetailUseCase
import com.example.kalanacommerce.domain.usecase.product.GetProductsUseCase
import com.example.kalanacommerce.domain.usecase.product.GetUnitDetailUseCase
import com.example.kalanacommerce.domain.usecase.product.GetUnitsUseCase
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

    factory { GetProductsUseCase(get()) }
    factory { GetOutletsUseCase(get()) }
    factory { GetCategoriesUseCase(get()) }
    factory { GetUnitsUseCase(get()) }

    factory { GetProductDetailUseCase(get()) }
    factory { GetOutletDetailUseCase(get()) }
    factory { GetCategoryDetailUseCase(get()) }
    factory { GetUnitDetailUseCase(get()) }

    factory { SendMessageUseCase(get(), get()) }

    factory { GetCartItemsUseCase(get()) }
    factory { AddToCartUseCase(get()) }
    factory { UpdateCartItemUseCase(get()) }
    factory { DeleteCartItemUseCase(get()) }
    factory { CheckoutUseCase(get()) }
    factory { DirectCheckoutUseCase(get()) }
    factory { GetOrdersUseCase(get()) }
    factory { GetOrderDetailUseCase(get()) }
    factory { UpdatePickupStatusUseCase(get()) }
}
