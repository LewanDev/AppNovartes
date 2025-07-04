package com.nmarchelli.appnovartes.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedidos_det")
data class PedidoDetEntity(
    @PrimaryKey val id: Int,
    val id_cab: Int,
    val articulo: String,
    val cantidad: Int,
    val precio: Double,
    val total_item: Double,
    val pos_fila: Int,
    val codigo: String,
    val observacion: String,
    val id_estado: Int,
    val _borrado: Int,
    val plano: String,
    val historial: String
)
