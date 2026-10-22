package com.example.order.domain.usecase

import com.example.cart.domain.usecase.AddCartItemsUseCase
import com.example.menu.domain.usecase.GetMenuSnapshotUseCase
import com.example.order.domain.model.Order
import com.example.order.domain.model.ReorderResult
import javax.inject.Inject

/** Puts a past order's items that are still on the menu back in the cart, at today's prices. */
class ReorderUseCase @Inject constructor(
    private val getMenuSnapshotUseCase: GetMenuSnapshotUseCase,
    private val addCartItemsUseCase: AddCartItemsUseCase,
) {
    suspend operator fun invoke(order: Order): ReorderResult = TODO()
}
