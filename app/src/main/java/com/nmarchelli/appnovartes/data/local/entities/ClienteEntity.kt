package com.nmarchelli.appnovartes.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey val codigo: Int,
    val nombreCliente: String,
    val idDocumento: Int,
    val numeroDto: String,
    val idProvincia: Int,
    val idLocalidad: Int,
    val codigoPostal: String,
    val domicilio: String,
    val mail: String,
    val telefono: String,
    val ctacte: String
)

