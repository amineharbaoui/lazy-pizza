package com.example.order.domain.usecase

import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartTopping
import com.example.cart.domain.model.CartWriteResult
import com.example.cart.domain.usecase.AddCartItemsUseCase
import com.example.menu.domain.model.MenuItem
import com.example.menu.domain.model.MenuSnapshot
import com.example.menu.domain.model.MenuSnapshotResult
import com.example.menu.domain.model.Topping
import com.example.menu.domain.usecase.GetMenuSnapshotUseCase
import com.example.order.domain.model.Order
import com.example.order.domain.model.OrderItem
import com.example.order.domain.model.ReorderError
import com.example.order.domain.model.ReorderResult
import com.example.order.domain.model.SkippedReorderItem
import java.util.UUID
import javax.inject.Inject

/** Puts a past order's items that are still on the menu back in the cart, at today's prices. */
class ReorderUseCase @Inject constructor(
    private val getMenuSnapshotUseCase: GetMenuSnapshotUseCase,
    private val addCartItemsUseCase: AddCartItemsUseCase,
) {
    suspend operator fun invoke(order: Order): ReorderResult {
        val menu = when (val result = getMenuSnapshotUseCase()) {
            is MenuSnapshotResult.Success -> result.snapshot
            MenuSnapshotResult.Unavailable -> return ReorderResult.Failed(ReorderError.MenuUnavailable)
        }
        // An empty menu is a failed load (e.g. offline with a cold cache), not "nothing available".
        if (menu.items.isEmpty()) return ReorderResult.Failed(ReorderError.MenuUnavailable)

        val skipped = mutableListOf<SkippedReorderItem>()
        val lines = order.items.mapNotNull { item -> menu.toCartLine(item, skipped) }
        if (lines.isEmpty()) return ReorderResult.NothingAvailable(skipped)

        return when (addCartItemsUseCase(lines)) {
            CartWriteResult.Success -> ReorderResult.Added(skipped)
            CartWriteResult.Failed -> ReorderResult.Failed(ReorderError.CartUpdateFailed)
        }
    }

    private fun MenuSnapshot.toCartLine(
        item: OrderItem,
        skipped: MutableList<SkippedReorderItem>,
    ): CartItem? {
        val product = items.firstOrNull { it.id == item.productId }
        if (product == null) {
            skipped += SkippedReorderItem.Product(item.name)
            return null
        }
        return when (product) {
            is MenuItem.PizzaItem -> CartItem.Pizza(
                lineId = UUID.randomUUID().toString(),
                productId = product.id,
                name = product.name,
                imageUrl = product.imageUrl,
                quantity = item.quantity,
                category = product.category,
                unitPrice = product.unitPrice,
                toppings = item.toppings.mapNotNull { pastTopping ->
                    val topping: Topping? = toppings.firstOrNull { it.id == pastTopping.id }
                    if (topping == null) skipped += SkippedReorderItem.Topping(pastTopping.name, product.name)
                    topping?.let { CartTopping(it.id, it.name, it.unitPrice, pastTopping.quantity) }
                },
            )

            is MenuItem.OtherMenuItem -> CartItem.Other(
                lineId = product.id,
                productId = product.id,
                name = product.name,
                imageUrl = product.imageUrl,
                quantity = item.quantity,
                category = product.category,
                unitPrice = product.unitPrice,
            )
        }
    }
}
