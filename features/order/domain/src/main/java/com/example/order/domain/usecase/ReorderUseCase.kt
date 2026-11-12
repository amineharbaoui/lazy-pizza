package com.example.order.domain.usecase

import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartTopping
import com.example.cart.domain.model.UnavailableItem
import com.example.cart.domain.repository.UnavailableItemsRepository
import com.example.cart.domain.usecase.MergeItemsIntoCartUseCase
import com.example.menu.domain.model.CurrentMenuResult
import com.example.menu.domain.model.MenuItem
import com.example.menu.domain.model.Topping
import com.example.menu.domain.usecase.GetCurrentMenuUseCase
import com.example.order.domain.model.Order
import com.example.order.domain.model.OrderItem
import com.example.order.domain.model.ReorderResult
import java.util.UUID
import javax.inject.Inject

class ReorderUseCase @Inject constructor(
    private val getCurrentMenuUseCase: GetCurrentMenuUseCase,
    private val mergeItemsIntoCartUseCase: MergeItemsIntoCartUseCase,
    private val unavailableItemsRepository: UnavailableItemsRepository,
) {
    suspend operator fun invoke(order: Order): ReorderResult {
        val menu = when (val result = getCurrentMenuUseCase()) {
            CurrentMenuResult.Unavailable -> return ReorderResult.MenuUnavailable
            is CurrentMenuResult.Loaded -> result
        }
        val menuItems = menu.items.associateBy { it.id }
        val toppings = menu.toppings.associateBy { it.id }

        val cartItems = mutableListOf<CartItem>()
        val unavailableItems = mutableListOf<UnavailableItem>()
        order.items.forEach { orderItem ->
            when (val menuItem = menuItems[orderItem.productId]) {
                null -> unavailableItems += UnavailableItem.Item(orderItem.name)
                is MenuItem.OtherMenuItem -> cartItems += menuItem.toCartItem(orderItem.quantity)
                is MenuItem.PizzaItem -> {
                    val (offered, missing) = orderItem.toppings.partition { it.id in toppings }
                    missing.forEach { unavailableItems += UnavailableItem.Topping(name = it.name, pizzaName = orderItem.name) }
                    cartItems += menuItem.toCartItem(orderItem, offered.map { toppings.getValue(it.id).toCartTopping(it.quantity) })
                }
            }
        }

        if (cartItems.isEmpty()) return ReorderResult.NothingAvailable

        mergeItemsIntoCartUseCase(cartItems)
        unavailableItemsRepository.saveUnavailableItems(unavailableItems)
        return ReorderResult.Added
    }

    private fun MenuItem.OtherMenuItem.toCartItem(quantity: Int) = CartItem.Other(
        lineId = newLineId(),
        productId = id,
        name = name,
        imageUrl = imageUrl,
        quantity = quantity,
        category = category,
        unitPrice = unitPrice,
    )

    private fun MenuItem.PizzaItem.toCartItem(
        orderItem: OrderItem,
        toppings: List<CartTopping>,
    ) = CartItem.Pizza(
        lineId = newLineId(),
        productId = id,
        name = name,
        imageUrl = imageUrl,
        quantity = orderItem.quantity,
        category = category,
        unitPrice = unitPrice,
        toppings = toppings,
    )

    private fun Topping.toCartTopping(quantity: Int) = CartTopping(
        id = id,
        name = name,
        unitPrice = unitPrice,
        quantity = quantity,
    )

    private fun newLineId(): String = UUID.randomUUID().toString()
}
