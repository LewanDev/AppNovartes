package com.nmarchelli.appnovartes.data.repository

import com.nmarchelli.appnovartes.data.local.dao.InformePedidoDao
import com.nmarchelli.appnovartes.data.remote.ApiService
import com.nmarchelli.appnovartes.domain.models.InformePedido
import com.nmarchelli.appnovartes.domain.models.toEntity
import kotlin.text.insert

class InformePedidoRepository(
    private val api: ApiService,
    private val dao: InformePedidoDao
) {
    suspend fun getLastInformePedido(): InformePedido {
        val response = api.getLastInformePedido()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return body
            } else {
                throw Exception("El cuerpo de la respuesta está vacío")
            }
        } else {
            throw Exception("Error al obtener pedidoscab desde la API")
        }
    }

    suspend fun insert(informePedido: InformePedido) {
        dao.insert(informePedido.toEntity())
    }
}


