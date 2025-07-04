package com.nmarchelli.appnovartes.data.repository

import com.nmarchelli.appnovartes.data.local.dao.PedidoCabDao
import com.nmarchelli.appnovartes.data.local.dao.PedidoDetDao
import com.nmarchelli.appnovartes.data.remote.ApiService
import com.nmarchelli.appnovartes.domain.models.PedidoCab
import com.nmarchelli.appnovartes.domain.models.PedidoDet
import com.nmarchelli.appnovartes.domain.models.toEntity

class PedidoDetRepository(
    private val api: ApiService,
    private val dao: PedidoDetDao
) {
    suspend fun getAll(): List<PedidoDet> {
        val response = api.getPedidosDet()
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

    suspend fun insert(pedidoDet: PedidoDet) {
        dao.insert(pedidoDet.toEntity())
    }
}