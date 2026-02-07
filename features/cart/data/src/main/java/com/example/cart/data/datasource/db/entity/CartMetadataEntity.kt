package com.example.cart.data.datasource.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_metadata")
data class CartMetadataEntity(
    @PrimaryKey val ownerKey: String,
    val lastUpdatedAtMillis: Long,
)
