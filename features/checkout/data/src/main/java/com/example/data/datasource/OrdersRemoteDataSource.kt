package com.example.data.datasource

import com.example.data.model.OrderDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrdersRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    suspend fun createOrder(order: OrderDto): Result<Unit> = runCatching {
        firestore.collection("orders")
            .document() // auto id
            .set(order)
            .await()
    }
}
