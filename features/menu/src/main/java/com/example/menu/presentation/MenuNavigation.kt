package com.example.menu.presentation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object MenuRoute : NavKey

@Serializable
data class PizzaDetailRoute(
    val productId: String,
) : NavKey

// TODO: to remove after refactoring
@Serializable data object CartRoute : NavKey
@Serializable data object HistoryRoute : NavKey