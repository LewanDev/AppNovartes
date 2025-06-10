package com.nmarchelli.appnovartes.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "configuraciones")
data class ConfiguracionEntity(
    @PrimaryKey val id: Int,
    val variable: String,
    val valor: String,
    val agrupado: String,
    val _borrado: Boolean
)
