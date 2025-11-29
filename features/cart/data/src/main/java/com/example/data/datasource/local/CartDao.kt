package com.example.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Transaction
    @Query("SELECT * FROM cart_items")
    fun observeCartLines(): Flow<List<CartLineWithToppings>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CartItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToppings(toppings: List<CartToppingEntity>)

    @Query("DELETE FROM cart_toppings WHERE lineId = :lineId")
    suspend fun deleteToppingsForLine(lineId: String)

    @Query("DELETE FROM cart_items WHERE lineId = :lineId")
    suspend fun deleteItem(lineId: String)

    @Update
    suspend fun updateItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items")
    suspend fun clearItems()

    @Query("DELETE FROM cart_toppings")
    suspend fun clearToppings()
}
