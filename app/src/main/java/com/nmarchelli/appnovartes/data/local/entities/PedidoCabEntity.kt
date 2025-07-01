package com.nmarchelli.appnovartes.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedidos_cab")
data class PedidoCabEntity(
    @PrimaryKey val id: String,
    val creado: String,
    val cliente: String,
    val observaciones: String,
    val precio: String,
    val _borrado: Int,
    val actualizacion: String?,
    val id_estado: String,
    val historial: String
)
