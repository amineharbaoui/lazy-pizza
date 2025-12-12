package com.example.data.datasource.mapper

import com.example.data.datasource.db.entity.CartItemEntity
import com.example.data.datasource.db.entity.CartItemType
import com.example.data.datasource.db.entity.CartLineWithToppings
import com.example.data.datasource.db.entity.CartToppingEntity
import com.example.domain.model.CartItem
import com.example.domain.model.CartItem.Other
import com.example.domain.model.CartItem.Pizza
import com.example.domain.model.CartTopping

fun CartLineWithToppings.toDomain(): CartItem = when (item.type) {
    CartItemType.OTHER -> Other(
        lineId = item.lineId,
        productId = item.productId,
        name = item.name,
        imageUrl = item.imageUrl,
        price = requireNotNull(item.otherItemPrice) { "unitPrice required for SIMPLE" },
        quantity = item.quantity,
    )

    CartItemType.PIZZA -> Pizza(
        lineId = item.lineId,
        productId = item.productId,
        name = item.name,
        imageUrl = item.imageUrl,
        basePrice = requireNotNull(item.pizzaItemPrice) { "basePrice required for PIZZA" },
        toppings = toppings.map { it.toDomain() },
        quantity = item.quantity,
    )
}

fun CartToppingEntity.toDomain(): CartTopping = CartTopping(
    toppingId = toppingId,
    name = name,
    price = price,
    quantity = quantity,
)

fun Other.toEntity(ownerKey: String): CartItemEntity = CartItemEntity(
    ownerKey = ownerKey,
    lineId = lineId,
    productId = productId,
    name = name,
    imageUrl = imageUrl,
    type = CartItemType.OTHER,
    otherItemPrice = price,
    pizzaItemPrice = null,
    quantity = quantity,
)

fun Pizza.toEntity(ownerKey: String): CartItemEntity = CartItemEntity(
    ownerKey = ownerKey,
    lineId = lineId,
    productId = productId,
    name = name,
    imageUrl = imageUrl,
    type = CartItemType.PIZZA,
    otherItemPrice = null,
    pizzaItemPrice = basePrice,
    quantity = quantity,
)

fun CartTopping.toEntity(
    ownerKey: String,
    lineId: String,
): CartToppingEntity = CartToppingEntity(
    ownerKey = ownerKey,
    lineId = lineId,
    toppingId = toppingId,
    name = name,
    price = price,
    quantity = quantity,
)
