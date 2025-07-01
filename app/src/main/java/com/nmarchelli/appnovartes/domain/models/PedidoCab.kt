package com.nmarchelli.appnovartes.domain.models

import com.nmarchelli.appnovartes.data.local.entities.PedidoCabEntity

class PedidoCab(
    val id: String,
    val creado: String,
    val cliente: String,
    val observaciones: String,
    val precio: String,
    val _borrado: Int,
    val actualizacion: String?,
    val id_estado: String,
    val historial: String
)

fun PedidoCab.toEntity(): PedidoCabEntity{
    return PedidoCabEntity(
        id = id,
        creado = creado,
        cliente = cliente,
        observaciones = observaciones,
        precio = precio,
        _borrado = _borrado,
        actualizacion = actualizacion,
        id_estado = id_estado,
        historial = historial,
    )
}