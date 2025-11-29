package com.example.data.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_metadata")
data class CartMetadataEntity(
    @PrimaryKey val id: Int = 0,
    val lastUpdatedAtMillis: Long,
)
