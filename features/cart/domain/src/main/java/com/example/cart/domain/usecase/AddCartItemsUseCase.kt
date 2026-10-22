package com.example.cart.domain.usecase

import com.example.auth.domain.repository.SessionRepository
import com.example.cart.domain.model.CartItem
import com.example.cart.domain.model.CartWriteResult
import com.example.cart.domain.repository.CartRepository
import javax.inject.Inject

/**
 * Adds the lines to the session's cart in one write. A non-pizza line whose product is already
 * in the cart adds its quantity to that line instead of replacing it. Pizzas always get a new line.
 */
class AddCartItemsUseCase @Inject constructor(
    private val repository: CartRepository,
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(items: List<CartItem>): CartWriteResult = TODO()
}
