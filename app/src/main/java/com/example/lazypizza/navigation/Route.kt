package com.example.lazypizza.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Route(
    val title: String,
) {
    @Serializable
    object Home {
        @Serializable
        object Menu : Route(title = "Menu")

        @Serializable
        object Cart : Route(title = "Cart")

        @Serializable
        object History : Route(title = "History")
    }

    @Serializable
    data class Detail(val productId: String) : Route(title = "Detail")
}