package com.example.domain.repository

import com.example.domain.model.Order

interface OrdersRepository {
    suspend fun placeOrder(order: Order): Result<Unit>
}
