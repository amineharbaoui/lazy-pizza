package com.example.data.datasource.mapper

import com.example.data.datasource.db.entity.CartItemEntity
import com.example.data.datasource.db.entity.CartLineWithToppings
import com.example.data.datasource.db.entity.CartToppingEntity
import com.example.domain.model.CartItem
import com.example.domain.model.CartTopping
import com.example.model.ProductCategory

fun CartLineWithToppings.toDomain(): CartItem = when (item.category) {
    ProductCategory.PIZZA -> CartItem.Pizza(
        lineId = item.lineId,
        productId = item.productId,
        name = item.name,
        imageUrl = item.imageUrl,
        unitPrice = requireNotNull(item.pizzaItemPrice) { "unitPrice required for PIZZA" },
        toppings = toppings.map { it.toDomain() },
        quantity = item.quantity,
        category = item.category,
    )

    else -> CartItem.Other(
        lineId = item.lineId,
        productId = item.productId,
        name = item.name,
        imageUrl = item.imageUrl,
        unitPrice = requireNotNull(item.otherItemPrice) { "unitPrice required for SIMPLE" },
        quantity = item.quantity,
        category = item.category,
    )
}

fun CartToppingEntity.toDomain(): CartTopping = CartTopping(
    toppingId = toppingId,
    name = name,
    unitPrice = unitPrice,
    quantity = quantity,
)

fun CartItem.Other.toEntity(ownerKey: String): CartItemEntity = CartItemEntity(
    ownerKey = ownerKey,
    lineId = lineId,
    productId = productId,
    name = name,
    imageUrl = imageUrl,
    otherItemPrice = unitPrice,
    pizzaItemPrice = null,
    quantity = quantity,
    category = category,
)

fun CartItem.Pizza.toEntity(ownerKey: String): CartItemEntity = CartItemEntity(
    ownerKey = ownerKey,
    lineId = lineId,
    productId = productId,
    name = name,
    imageUrl = imageUrl,
    otherItemPrice = null,
    pizzaItemPrice = unitPrice,
    quantity = quantity,
    category = category,
)

fun CartTopping.toEntity(
    ownerKey: String,
    lineId: String,
): CartToppingEntity = CartToppingEntity(
    ownerKey = ownerKey,
    lineId = lineId,
    toppingId = toppingId,
    name = name,
    unitPrice = unitPrice,
    quantity = quantity,
)
