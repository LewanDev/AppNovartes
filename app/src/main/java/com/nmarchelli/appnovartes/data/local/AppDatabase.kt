package com.nmarchelli.appnovartes.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nmarchelli.appnovartes.data.local.dao.ArticuloDao
import com.nmarchelli.appnovartes.data.local.dao.CartDao
import com.nmarchelli.appnovartes.data.local.dao.ClienteDao
import com.nmarchelli.appnovartes.data.local.entities.ArticuloEntity
import com.nmarchelli.appnovartes.data.local.entities.CartItemEntity
import com.nmarchelli.appnovartes.data.local.entities.ClienteEntity

@Database(entities = [ArticuloEntity::class, ClienteEntity::class, CartItemEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articuloDao(): ArticuloDao
    abstract fun clienteDao(): ClienteDao
    abstract fun cartDao(): CartDao

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