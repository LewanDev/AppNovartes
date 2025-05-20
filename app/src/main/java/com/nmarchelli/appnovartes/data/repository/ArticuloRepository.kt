package com.nmarchelli.appnovartes.data.repository

import com.nmarchelli.appnovartes.data.local.dao.ArticuloDao
import com.nmarchelli.appnovartes.data.local.entities.ArticuloEntity
import com.nmarchelli.appnovartes.data.remote.ApiService
import com.nmarchelli.appnovartes.domain.models.Articulo
import com.nmarchelli.appnovartes.domain.models.toEntity

class ArticuloRepository(
    private val api: ApiService,
    private val dao: ArticuloDao
) {

    // Obtener desde la API y mapear a modelo de dominio
    suspend fun getArticulos(): List<Articulo> {
        val response = api.getArticulos()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Error al obtener artículos desde la API")
        }
    }

    // Obtener desde Room y devolver entidades
    suspend fun getArticulosLocal(): List<ArticuloEntity> {
        return dao.getAll()
    }

    // Guardar en Room
    suspend fun insertAll(articulos: List<Articulo>) {
        dao.insertAll(articulos.map { it.toEntity() })
    }
}