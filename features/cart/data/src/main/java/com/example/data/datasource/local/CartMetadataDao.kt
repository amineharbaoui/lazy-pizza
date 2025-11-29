package com.example.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CartMetadataDao {

    @Query("SELECT * FROM cart_metadata WHERE id = 0")
    suspend fun getMetadata(): CartMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(metadata: CartMetadataEntity)

    @Query("DELETE FROM cart_metadata")
    suspend fun clear()
}
