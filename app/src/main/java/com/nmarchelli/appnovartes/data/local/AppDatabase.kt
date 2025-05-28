package com.nmarchelli.appnovartes.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nmarchelli.appnovartes.data.local.dao.ArticuloDao
import com.nmarchelli.appnovartes.data.local.dao.ClienteDao
import com.nmarchelli.appnovartes.data.local.entities.ArticuloEntity
import com.nmarchelli.appnovartes.data.local.entities.ClienteEntity

@Database(entities = [ArticuloEntity::class, ClienteEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articuloDao(): ArticuloDao
    abstract fun clienteDao(): ClienteDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "novartes.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build().also { instance = it }
            }
    }
}