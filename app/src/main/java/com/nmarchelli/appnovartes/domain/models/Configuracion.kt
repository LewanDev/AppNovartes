package com.nmarchelli.appnovartes.domain.models

import com.nmarchelli.appnovartes.data.local.entities.ConfiguracionEntity

class Configuracion(
    val id: Int,
    val variable: String,
    val valor: String,
    val agrupado: String,
    val _borrado: Boolean
)

fun Configuracion.toEntity(): ConfiguracionEntity {
    return ConfiguracionEntity(
        id = id,
        variable = variable,
        valor = valor,
        agrupado = agrupado,
        _borrado = _borrado
    )
}

