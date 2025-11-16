package com.example.core.firebase.firestore.datasource

import com.example.core.firebase.firestore.dto.ProductRemoteDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ProductDatasource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    fun observeSections(): Flow<List<ProductRemoteDto>> = callbackFlow {
        val registration: ListenerRegistration =
            firestore.collectionGroup("products")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val items = snapshot
                        ?.documents
                        ?.mapNotNull { it.toObject(ProductRemoteDto::class.java) }
                        .orEmpty()

                    trySend(items).isSuccess
                }

        awaitClose { registration.remove() }
    }

    fun observeProductsInCategory(categoryId: String): Flow<List<ProductRemoteDto>> = callbackFlow {
        val reg = firestore
            .collection("categories")
            .document(categoryId)
            .collection("products")
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }
                val items = snap?.documents
                    ?.mapNotNull { it.toObject(ProductRemoteDto::class.java) }
                    .orEmpty()
                trySend(items).isSuccess
            }
        awaitClose { reg.remove() }
    }

    fun observeProductById(productId: String): Flow<ProductRemoteDto?> = callbackFlow {
        val reg = firestore
            .collectionGroup("products")
            .whereEqualTo("id", productId)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val dto = snapshot?.documents?.firstOrNull()
                    ?.toObject(ProductRemoteDto::class.java)
                    ?.copy(
                        id = snapshot.documents.firstOrNull()?.id ?: productId,
                        category = snapshot.documents.firstOrNull()?.getString("category").orEmpty()
                    )
                trySend(dto).isSuccess
            }
        awaitClose { reg.remove() }
    }
}