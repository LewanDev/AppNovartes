package com.nmarchelli.appnovartes.data.api

import com.nmarchelli.appnovartes.data.model.Articulo
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("kotlinapp/get_articulos.php")
    suspend fun getArticulos(): Response<List<Articulo>>
}