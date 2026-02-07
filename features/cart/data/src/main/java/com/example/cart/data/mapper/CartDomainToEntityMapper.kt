package com.example.cart.data.mapper

import com.example.cart.data.datasource.db.entity.CartItemEntity
import com.example.cart.data.datasource.db.entity.CartToppingEntity
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartTopping
import javax.inject.Inject

class CartDomainToEntityMapper @Inject constructor() {

    fun mapItem(input: CartItem, ownerKey: String): CartItemEntity = when (input) {
        is CartItem.Other -> CartItemEntity(
            ownerKey = ownerKey,
            lineId = input.lineId,
            productId = input.productId,
            name = input.name,
            imageUrl = input.imageUrl,
            otherItemPrice = input.unitPrice,
            pizzaItemPrice = null,
            quantity = input.quantity,
            category = input.category,
        )

        is CartItem.Pizza -> CartItemEntity(
            ownerKey = ownerKey,
            lineId = input.lineId,
            productId = input.productId,
            name = input.name,
            imageUrl = input.imageUrl,
            otherItemPrice = null,
            pizzaItemPrice = input.unitPrice,
            quantity = input.quantity,
            category = input.category,
        )
    }

    fun mapTopping(
        input: CartTopping,
        ownerKey: String,
        lineId: String,
    ): CartToppingEntity = CartToppingEntity(
        ownerKey = ownerKey,
        lineId = lineId,
        toppingId = input.id,
        name = input.name,
        unitPrice = input.unitPrice,
        quantity = input.quantity,
    )

    fun mapToppings(
        input: List<CartTopping>,
        ownerKey: String,
        lineId: String,
    ): List<CartToppingEntity> = input.map { mapTopping(it, ownerKey, lineId) }
}
