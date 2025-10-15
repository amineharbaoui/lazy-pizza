package com.example.lazypizza.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {

    @Serializable
    object Home : Route

    @Serializable
    data class Detail(val productId: String) : Route
}