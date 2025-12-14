package com.example.uilogin.pizzadetail

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class PizzaDetailRoute(
    val productId: String,
) : NavKey
