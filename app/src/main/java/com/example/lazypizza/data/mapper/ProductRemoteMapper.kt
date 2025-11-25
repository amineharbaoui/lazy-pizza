package com.example.lazypizza.data.mapper

import com.example.core.firebase.firestore.dto.ProductRemoteDto
import com.example.lazypizza.features.home.domain.models.Product
import com.example.menu.domain.model.ProductCategory

fun ProductRemoteDto.toDomain(): Product {
    val cat = runCatching { ProductCategory.valueOf(category) }.getOrElse { ProductCategory.PIZZA }
    return Product(
        id = id,
        name = name,
        description = description,
        basePrice = price,
        category = cat,
        imageUrl = imageUrl
    )
}
