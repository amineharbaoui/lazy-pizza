package com.example.cart.domain.usecase

import com.example.auth.domain.repository.SessionRepository
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MergeItemsIntoCartUseCase @Inject constructor(
    private val repository: CartRepository,
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(items: List<CartItem>) {
        val owner = sessionRepository.currentCartOwner()
        val cartLines = repository.observeCart(owner).first().items

        val lines = cartLines.toMutableList()
        items.forEach { item ->
            val index = lines.indexOfFirst { it.isSameItemAs(item) }
            if (index == -1) {
                lines += item
            } else {
                lines[index] = item.withLine(lineId = lines[index].lineId, quantity = lines[index].quantity + item.quantity)
            }
        }

        lines.take(cartLines.size)
            .zip(cartLines)
            .filter { (line, before) -> line != before }
            .forEach { (line, _) -> repository.replaceItem(owner = owner, item = line) }
        lines.drop(cartLines.size).forEach { line -> repository.addItem(owner = owner, item = line) }
    }

    private fun CartItem.isSameItemAs(other: CartItem): Boolean = when (this) {
        is CartItem.Other -> other is CartItem.Other && productId == other.productId
        is CartItem.Pizza -> other is CartItem.Pizza && productId == other.productId && toppingQuantities() == other.toppingQuantities()
    }

    private fun CartItem.Pizza.toppingQuantities(): Map<String, Int> = toppings
        .filter { it.quantity > 0 }
        .associate { it.id to it.quantity }

    private fun CartItem.withLine(
        lineId: String,
        quantity: Int,
    ): CartItem = when (this) {
        is CartItem.Other -> copy(lineId = lineId, quantity = quantity)
        is CartItem.Pizza -> copy(lineId = lineId, quantity = quantity)
    }
}
