package com.nmarchelli.appnovartes.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "informepedido")
data class InformePedidoEntity(
    @PrimaryKey val id: Int,
    val pedido: String,
    val estado: String,
    val fecha: String,
    val cliente: String,
    val email: String,
    val codigo: String,
    val descripcion: String,
    val precio: String,
    val cantidad: String,
    val patas: String,
    val telas: String,
    val observacion: String,
    val historial: String,
    val idPedido: Int,
    val idestado: Int,
    val idcliente: Int,
    val orden: String
)
