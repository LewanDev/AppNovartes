package com.nmarchelli.appnovartes.domain.mappers

import com.nmarchelli.appnovartes.data.local.entities.ConfiguracionEntity
import com.nmarchelli.appnovartes.domain.models.Configuracion

fun ConfiguracionEntity.toDomain(): Configuracion {
    return Configuracion(
        id = id,
        variable = variable,
        valor = valor,
        agrupado = agrupado,
        _borrado = _borrado
    )
}

fun List<ConfiguracionEntity>.toDomainList(): List<Configuracion> {
    return map { it.toDomain() }
}