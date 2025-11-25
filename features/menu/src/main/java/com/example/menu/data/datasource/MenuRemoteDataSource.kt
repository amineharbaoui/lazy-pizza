package com.example.menu.data.datasource

import com.example.menu.data.model.ProductDto
import com.example.menu.domain.model.ProductCategory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class MenuRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    fun observeProductsByCategory(category: ProductCategory): Flow<List<ProductDto>> =
        callbackFlow {
            val listener = firestore.collection("products")
                .whereEqualTo("category", category.name)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val list = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(ProductDto::class.java)?.apply { id = doc.id }
                    } ?: emptyList()

                    trySend(list)
                }
            awaitClose { listener.remove() }
        }

    fun observeProductById(id: String): Flow<ProductDto?> =
        callbackFlow {
            val listener = firestore.collection("products")
                .document(id)
                .addSnapshotListener { doc, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    trySend(doc?.toObject(ProductDto::class.java)?.apply { this.id = doc.id })
                }

            awaitClose { listener.remove() }
        }
}