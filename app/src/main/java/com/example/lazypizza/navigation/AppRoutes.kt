package com.example.lazypizza.navigation

import android.os.Parcelable
import androidx.navigation3.runtime.NavKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

sealed interface AppRoute : NavKey, Parcelable

@Parcelize
@Serializable
data object MenuRoute : AppRoute

@Parcelize
@Serializable
data object AuthRoute : AppRoute

@Parcelize
@Serializable
data object CartRoute : AppRoute

@Parcelize
@Serializable
data object CheckoutRoute : AppRoute

@Parcelize
@Serializable
data object OrdersRoute : AppRoute

@Serializable
sealed interface PizzaDetailRoute : AppRoute {
    val productId: String

    @Parcelize
    @Serializable
    data class Create(override val productId: String) : PizzaDetailRoute

    @Parcelize
    @Serializable
    data class Edit(override val productId: String, val lineId: String) : PizzaDetailRoute
}
