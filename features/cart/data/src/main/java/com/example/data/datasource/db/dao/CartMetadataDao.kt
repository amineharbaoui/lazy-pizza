package com.example.data.datasource.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.datasource.db.entity.CartMetadataEntity

@Dao
interface CartMetadataDao {

    @Query("SELECT * FROM cart_metadata WHERE ownerKey = :ownerKey")
    suspend fun getMetadata(ownerKey: String): CartMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun upsert(metadata: CartMetadataEntity)

    @Query("DELETE FROM cart_metadata WHERE ownerKey = :ownerKey")
    suspend fun clear(ownerKey: String)
}
