package com.nmarchelli.appnovartes.domain.models

class Usuario(
    val id: Int,
    val descripcion: String,
    val usuario: String,
    val contrasena: String,
    val tipo: Int,
    val actualizacion: String,
    val creado: String,
    val configuracion: String,
    val _borrado: Boolean
)