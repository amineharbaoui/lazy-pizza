package com.example.cart.domain.usecase

import com.example.cart.domain.model.Cart
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartOwner
import com.example.core.model.ProductCategory

internal const val USER_ID = "user-123"
internal const val LINE_ID = "line-1"
internal val USER_OWNER = CartOwner.User(USER_ID)

internal fun cartItem(
    lineId: String = LINE_ID,
    quantity: Int = 1,
): CartItem = CartItem.Other(
    lineId = lineId,
    productId = "product-1",
    name = "Cola",
    imageUrl = "https://example.com/cola.png",
    quantity = quantity,
    category = ProductCategory.DRINK,
    unitPrice = 2.5,
)

internal fun cartOf(vararg items: CartItem): Cart = Cart(items.toList())
