package com.nmarchelli.appnovartes.data.repository

import com.nmarchelli.appnovartes.data.local.dao.PedidoCabDao
import com.nmarchelli.appnovartes.data.remote.ApiService
import com.nmarchelli.appnovartes.domain.models.PedidoCab
import com.nmarchelli.appnovartes.domain.models.toEntity

class PedidoCabRepository(
    private val api: ApiService,
    private val dao: PedidoCabDao
) {
    suspend fun getLastPedidoCab(): PedidoCab {
        val response = api.getLastPedidosCab()
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

    suspend fun insert(pedidoCab: PedidoCab){
        dao.insert(pedidoCab.toEntity())
    }
}