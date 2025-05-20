package com.nmarchelli.appnovartes.data.remote

import com.nmarchelli.appnovartes.domain.models.Articulo
import com.nmarchelli.appnovartes.domain.models.Rubro
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("kotlinapp/get_articulos.php")
    suspend fun getArticulos(): Response<List<Articulo>>

    @GET("kotlinapp/get_rubros.php")
    suspend fun getRubros(): Response<List<Rubro>>
}