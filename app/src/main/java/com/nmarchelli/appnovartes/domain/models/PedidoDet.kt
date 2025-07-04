package com.nmarchelli.appnovartes.domain.models

import com.nmarchelli.appnovartes.data.local.entities.PedidoDetEntity

class PedidoDet(
    val id: Int,
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

fun PedidoDet.toEntity(): PedidoDetEntity{
    return PedidoDetEntity(
        id = id,
        id_cab = id_cab,
        articulo = articulo,
        cantidad = cantidad,
        precio = precio,
        total_item = total_item,
        pos_fila = pos_fila,
        codigo = codigo,
        observacion = observacion,
        id_estado = id_estado,
        _borrado = _borrado,
        plano = plano,
        historial = historial
    )
}