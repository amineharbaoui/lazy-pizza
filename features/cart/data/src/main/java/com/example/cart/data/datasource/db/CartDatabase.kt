package com.example.cart.data.datasource.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cart.data.datasource.db.dao.CartDao
import com.example.cart.data.datasource.db.dao.CartMetadataDao
import com.example.cart.data.datasource.db.entity.CartItemEntity
import com.example.cart.data.datasource.db.entity.CartMetadataEntity
import com.example.cart.data.datasource.db.entity.CartToppingEntity

@Database(
    entities = [
        CartItemEntity::class,
        CartToppingEntity::class,
        CartMetadataEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun cartMetadataDao(): CartMetadataDao
}
