package com.nmarchelli.appnovartes.domain.models

class Cliente(
    val id: Int,
    val codigo: Int,
    val id_documento: Int,
    val numero_dto: Int,
    val razon_social: String,
    val id_provincia: Int,
    val id_localidad: Int,
    val codigo_postal: String,
    val codigo_cliente: String,
    val domicilio: String,
    val mail: String,
    val telefono: String,
    val creado: String,
    val actualizacion: String,
    val _borrado: Boolean
)