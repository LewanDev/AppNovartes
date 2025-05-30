package com.nmarchelli.appnovartes.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nmarchelli.appnovartes.data.local.entities.CartItemEntity

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CartItemEntity)

    @Query("SELECT * FROM cart_items")
    suspend fun getAllItems(): List<CartItemEntity>

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Delete
    suspend fun deleteItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteItemById(id: Int)


    @Query("UPDATE cart_items SET cantidad = :cantidad WHERE codigo = :codigo")
    suspend fun updateCantidad(codigo: String, cantidad: Int)

    @Query("SELECT * FROM cart_items WHERE codigo = :codigo LIMIT 1")
    suspend fun getItemByCodigo(codigo: String): CartItemEntity?

    @Query("UPDATE cart_items SET cantidad = cantidad - 1 WHERE id = :id AND cantidad > 1")
    suspend fun decreaseCantidad(id: Int)
}
