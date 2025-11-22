package com.example.core.firebase.firestore.dto

import androidx.annotation.Keep

@Keep
data class ProductRemoteDto(
    val id: String = "",
    val category: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = ""
)
