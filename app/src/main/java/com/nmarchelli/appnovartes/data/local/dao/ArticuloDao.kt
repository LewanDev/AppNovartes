package com.nmarchelli.appnovartes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nmarchelli.appnovartes.data.local.entities.ArticuloEntity

@Dao
interface ArticuloDao {
    @Query("SELECT * FROM articulos")
    suspend fun getAll(): List<ArticuloEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articulos: List<ArticuloEntity>)


    @Query("DELETE FROM articulos")
    suspend fun deleteAll()

}