package com.nmarchelli.appnovartes.domain.models

import com.nmarchelli.appnovartes.data.local.entities.InformePedidoEntity

class InformePedido(
    val id: Int,
    val pedido: String,
    val orden: String,
    val estado: String,
    val fecha: String,
    val cliente: String,
    val email: String,
    val codigo: String,
    val descripcion: String,
    val precio: String,
    val cantidad: String,
    val patas: String,
    val telas: String,
    val observacion: String,
    val historial: String,
    val idpedido: Int,
    val idestado: Int,
    val idcliente: Int
)

fun InformePedido.toEntity(): InformePedidoEntity {
    return InformePedidoEntity(
        id = id,
        pedido = pedido,
        orden = orden,
        estado = estado,
        fecha = fecha,
        cliente = cliente,
        email = email,
        codigo = codigo,
        descripcion = descripcion,
        precio = precio,
        cantidad = cantidad,
        patas = patas,
        telas = telas,
        observacion = observacion,
        historial = historial,
        idPedido = idpedido,
        idestado = idestado,
        idcliente = idcliente
    )
}