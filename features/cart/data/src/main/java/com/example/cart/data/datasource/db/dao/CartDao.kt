package com.example.cart.data.datasource.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.cart.data.datasource.db.entity.CartItemEntity
import com.example.cart.data.datasource.db.entity.CartLineWithToppings
import com.example.cart.data.datasource.db.entity.CartToppingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Transaction
    @Query("SELECT * FROM cart_items WHERE ownerKey = :ownerKey")
    fun observeCartLines(ownerKey: String): Flow<List<CartLineWithToppings>>

    @Transaction
    @Query("SELECT * FROM cart_items WHERE ownerKey = :ownerKey")
    suspend fun getCartLinesOnce(ownerKey: String): List<CartLineWithToppings>

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

    @Query("DELETE FROM cart_items WHERE ownerKey = :ownerKey")
    suspend fun clearItems(ownerKey: String)

    @Query("DELETE FROM cart_toppings WHERE ownerKey = :ownerKey")
    suspend fun clearToppings(ownerKey: String)
}
