package com.example.cart.data.mapper

import com.example.cart.data.datasource.db.entity.CartLineWithToppings
import com.example.cart.data.datasource.db.entity.CartToppingEntity
import com.example.domain.model.CartItem
import com.example.domain.model.CartTopping
import com.example.model.ProductCategory
import javax.inject.Inject

class CartEntityToDomainMapper @Inject constructor() {

    fun map(input: CartLineWithToppings): CartItem = when (input.item.category) {
        ProductCategory.PIZZA -> CartItem.Pizza(
            lineId = input.item.lineId,
            productId = input.item.productId,
            name = input.item.name,
            imageUrl = input.item.imageUrl,
            unitPrice = requireNotNull(input.item.pizzaItemPrice) { "unitPrice required for PIZZA" },
            toppings = input.toppings.map(::mapTopping),
            quantity = input.item.quantity,
            category = input.item.category,
        )

        else -> CartItem.Other(
            lineId = input.item.lineId,
            productId = input.item.productId,
            name = input.item.name,
            imageUrl = input.item.imageUrl,
            unitPrice = requireNotNull(input.item.otherItemPrice) { "unitPrice required for SIMPLE" },
            quantity = input.item.quantity,
            category = input.item.category,
        )
    }

    private fun mapTopping(input: CartToppingEntity): CartTopping = CartTopping(
        id = input.toppingId,
        name = input.name,
        unitPrice = input.unitPrice,
        quantity = input.quantity,
    )

    fun mapList(input: List<CartLineWithToppings>): List<CartItem> = input.map(::map)
}
