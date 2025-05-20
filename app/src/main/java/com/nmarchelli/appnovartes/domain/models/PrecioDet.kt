package com.nmarchelli.appnovartes.domain.models

class PrecioDet(
    val id: Int,
    val id_cab: Int,
    val articulo: String,
    val precio: Float,
    val precio_ant: Float,
    val creado: String,
    val actualizacion: String,
    val _borrado: Boolean
)