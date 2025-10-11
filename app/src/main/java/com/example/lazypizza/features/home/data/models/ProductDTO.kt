package com.example.lazypizza.features.home.data.models

import androidx.annotation.Keep

@Keep
data class ProductDTO(
    val id: String = "",
    val category: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = ""
)