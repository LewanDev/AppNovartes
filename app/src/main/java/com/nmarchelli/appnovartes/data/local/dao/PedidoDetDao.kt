package com.nmarchelli.appnovartes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nmarchelli.appnovartes.data.local.entities.PedidoDetEntity

@Dao
interface PedidoDetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pedidoDetEntity: PedidoDetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(listPedidoDet: List<PedidoDetEntity>)

    @Query("SELECT * FROM pedidos_det")
    suspend fun getAll(): List<PedidoDetEntity>
}