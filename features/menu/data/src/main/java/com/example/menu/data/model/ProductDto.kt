package com.example.menu.data.model

import androidx.annotation.Keep
import com.google.firebase.firestore.Exclude

@Keep
data class ProductDto(
    @get:Exclude @set:Exclude
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var imageUrl: String = "",
    var price: Double = 0.0,
    var category: String = "",
)
