package com.nmarchelli.appnovartes.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articulos")
data class ArticuloEntity(
    @PrimaryKey val id: Int,
    val codigo: String?,
    val descripcion: String,
    val descripcion_larga: String,
    val rubro: String?,
    val subrubro: String?,
    val destacado: Boolean,
    val _borrado: Boolean,
    val actualizacion: String, //DATETIME
    val UxB: Int,
    val cod_bar_unidad: String,
    val cod_bar_pack: String,
    val cod_bar_bulto: String,
    val UxP: Int,
    val creado: String, //TIMESTAMP
    val nodisponible: Boolean,
    val stockmax: Int,
    val principal: Boolean,
    val complemento: Boolean,
    val customizable: Boolean
)
