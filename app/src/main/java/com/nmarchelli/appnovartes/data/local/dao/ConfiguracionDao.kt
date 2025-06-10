package com.nmarchelli.appnovartes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nmarchelli.appnovartes.data.local.entities.ConfiguracionEntity

@Dao
interface ConfiguracionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(configs: List<ConfiguracionEntity>)

    @Query("SELECT * FROM configuraciones")
    suspend fun getAll(): List<ConfiguracionEntity>

    @Query("SELECT * FROM configuraciones WHERE variable IN (:variables)")
    suspend fun getConfiguraciones(variables: List<String>): List<ConfiguracionEntity>
}
