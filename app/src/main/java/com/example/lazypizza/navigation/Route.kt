package com.example.lazypizza.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {

    @Serializable
    object Menu : Route

    @Serializable
    data class Detail(val productId: String) : Route

    @Serializable
    object Cart : Route

    @Serializable
    object History : Route
}