package com.example.lazypizza.features.home.data.datasource

import com.example.lazypizza.features.home.data.models.ProductDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun observeSections(): Flow<List<ProductDTO>> = callbackFlow {
        val registration: ListenerRegistration =
            firestore.collectionGroup("products")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val items = snapshot
                        ?.documents
                        ?.mapNotNull { it.toObject(ProductDTO::class.java) }
                        .orEmpty()

                    trySend(items).isSuccess
                }

        awaitClose { registration.remove() }
    }
}
