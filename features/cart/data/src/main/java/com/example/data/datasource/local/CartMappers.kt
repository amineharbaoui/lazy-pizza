package com.example.data.datasource.local

import com.example.domain.model.CartItem
import com.example.domain.model.CartTopping
import com.example.domain.model.OtherCartItem
import com.example.domain.model.PizzaCartItem

fun CartLineWithToppings.toDomain(): CartItem = when (item.type) {
    CartItemType.OTHER -> OtherCartItem(
        lineId = item.lineId,
        productId = item.productId,
        name = item.name,
        imageUrl = item.imageUrl,
        price = requireNotNull(item.otherItemPrice) { "unitPrice required for SIMPLE" },
        quantity = item.quantity,
    )

    CartItemType.PIZZA -> PizzaCartItem(
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

fun OtherCartItem.toEntity(): CartItemEntity = CartItemEntity(
    lineId = lineId,
    productId = productId,
    name = name,
    imageUrl = imageUrl,
    type = CartItemType.OTHER,
    otherItemPrice = price,
    pizzaItemPrice = null,
    quantity = quantity,
)

fun PizzaCartItem.toEntity(): CartItemEntity = CartItemEntity(
    lineId = lineId,
    productId = productId,
    name = name,
    imageUrl = imageUrl,
    type = CartItemType.PIZZA,
    otherItemPrice = null,
    pizzaItemPrice = basePrice,
    quantity = quantity,
)

fun CartTopping.toEntity(lineId: String): CartToppingEntity = CartToppingEntity(
    lineId = lineId,
    toppingId = toppingId,
    name = name,
    price = price,
    quantity = quantity,
)
