package com.nmarchelli.appnovartes.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productoId: Int,
    val codigo: String,
    val nombre: String,
    val telas: String,
    val telas2: String,
    val patas: String,
    val patas2: String,
    val cantidad: Int
)

