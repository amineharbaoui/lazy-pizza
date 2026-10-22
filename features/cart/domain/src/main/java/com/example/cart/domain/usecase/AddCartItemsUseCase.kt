package com.example.cart.domain.usecase

import com.example.auth.domain.repository.SessionRepository
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartWriteResult
import com.example.cart.domain.repository.CartRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Adds the lines to the session's cart in one write. A non-pizza line whose product is already
 * in the cart adds its quantity to that line instead of replacing it. Pizzas always get a new line.
 */
class AddCartItemsUseCase @Inject constructor(
    private val repository: CartRepository,
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(items: List<CartItem>): CartWriteResult {
        val owner = sessionRepository.currentCartOwner()
        // A non-pizza line's lineId is its productId, so the write replaces the existing line.
        val quantities = repository.observeCart(owner).first().items
            .filterIsInstance<CartItem.Other>()
            .associate { it.lineId to it.quantity }
            .toMutableMap()
        val merged = items.map { item ->
            if (item !is CartItem.Other) return@map item
            val quantity = quantities.getOrDefault(item.lineId, 0) + item.quantity
            quantities[item.lineId] = quantity
            item.copy(quantity = quantity)
        }.asReversed().distinctBy { it.lineId }.asReversed()
        return repository.addItems(owner = owner, items = merged)
    }
}
