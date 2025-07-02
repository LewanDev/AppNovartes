package com.nmarchelli.appnovartes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nmarchelli.appnovartes.data.local.entities.InformePedidoEntity

@Dao
interface InformePedidoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(informePedidoEntity: InformePedidoEntity)

    @Query("SELECT * FROM informepedido ORDER BY idpedido DESC LIMIT 1")
    suspend fun getLastInformePedido(): InformePedidoEntity?

}