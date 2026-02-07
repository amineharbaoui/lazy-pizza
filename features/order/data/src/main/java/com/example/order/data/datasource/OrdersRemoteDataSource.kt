package com.example.order.data.datasource

import com.example.order.data.model.OrderDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrdersRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    suspend fun createOrder(order: OrderDto): Result<Unit> = runCatching {
        firestore.collection("orders")
            .document()
            .set(order)
            .await()
    }

    fun observeOrdersByUser(userId: String): Flow<List<OrderDto>> = callbackFlow {
        val listener = firestore.collection("orders")
            .whereEqualTo("userId", userId)
            .orderBy("createdAtEpochMs", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                error?.let {
                    close(it)
                    return@addSnapshotListener
                }

                val orders = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(OrderDto::class.java)
                }.orEmpty()

                trySend(orders)
            }

        awaitClose { listener.remove() }
    }
}
