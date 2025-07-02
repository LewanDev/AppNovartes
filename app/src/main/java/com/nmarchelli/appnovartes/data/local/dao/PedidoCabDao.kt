package com.nmarchelli.appnovartes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nmarchelli.appnovartes.data.local.entities.PedidoCabEntity

@Dao
interface PedidoCabDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pedidoCab: PedidoCabEntity)

    @Query("SELECT * FROM  pedidos_cab ORDER BY id DESC LIMIT 1")
    suspend fun getLastPedidosCab(): PedidoCabEntity?
}