package com.nmarchelli.appnovartes.domain.mappers

import com.nmarchelli.appnovartes.domain.models.Articulo
import com.nmarchelli.appnovartes.data.local.entities.ArticuloEntity

fun ArticuloEntity.toDomain(): Articulo {
    return Articulo(
        id = this.id,
        codigo = this.codigo,
        descripcion = this.descripcion,
        rubro = this.rubro,
        subrubro = this.subrubro,
        descripcion_larga = this.descripcion_larga,
        destacado = this.destacado,
        _borrado = this._borrado,
        actualizacion = this.actualizacion,
        UxB = this.UxB,
        cod_bar_unidad = this.cod_bar_unidad,
        cod_bar_pack = this.cod_bar_pack,
        cod_bar_bulto = this.cod_bar_bulto,
        UxP = this.UxP,
        creado = this.creado,
        nodisponible = this.nodisponible,
        stockmax = this.stockmax,
        principal = this.principal,
        complemento = this.complemento,
        customizable = this.customizable
    )
}

fun List<ArticuloEntity>.toDomainList(): List<Articulo> =
    this.map { it.toDomain() }
