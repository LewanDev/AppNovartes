package com.nmarchelli.appnovartes.data.repository

import com.nmarchelli.appnovartes.data.local.dao.ConfiguracionDao
import com.nmarchelli.appnovartes.data.local.entities.ConfiguracionEntity
import com.nmarchelli.appnovartes.data.remote.ApiService
import com.nmarchelli.appnovartes.domain.models.Configuracion
import com.nmarchelli.appnovartes.domain.models.toEntity

class ConfiguracionRepository(
    private val api: ApiService,
    private val dao: ConfiguracionDao
) {
    suspend fun getConfiguraciones(): List<Configuracion>{
        val response = api.getConfiguraciones()
        if(response.isSuccessful){
            return response.body() ?: emptyList()
        }else{
            throw Exception("Error al obtener configuraciones desde la API")
        }
    }

    suspend fun getConfiguracionesLocal(): List<ConfiguracionEntity>{
        return dao.getAll()
    }

    suspend fun insertAll(configuraciones: List<Configuracion>){
        dao.insertAll(configuraciones.map { it.toEntity()})
    }
}