package com.nmarchelli.appnovartes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nmarchelli.appnovartes.data.local.entities.ClienteEntity

@Dao
interface ClienteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cliente: ClienteEntity)

    @Query("SELECT * FROM clientes LIMIT 1")
    suspend fun getCliente(): ClienteEntity?

    @Query("DELETE FROM clientes")
    suspend fun deleteAll()
}
