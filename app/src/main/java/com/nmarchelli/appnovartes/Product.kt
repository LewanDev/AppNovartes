package com.nmarchelli.appnovartes

import java.io.Serializable

data class Product(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val precio: Int,
    val tela: String,
    val colorTela: String,
    val patas: String,
    val colorPatas: String,
    val medidas: String,
    val costura: String
): Serializable
