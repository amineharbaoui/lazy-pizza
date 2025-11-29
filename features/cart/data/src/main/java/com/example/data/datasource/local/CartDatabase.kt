package com.example.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        CartItemEntity::class,
        CartToppingEntity::class,
        CartMetadataEntity::class
    ],
    version = 1,
    exportSchema = false,
)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun cartMetadataDao(): CartMetadataDao
}
