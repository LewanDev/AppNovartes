package com.nmarchelli.appnovartes.data.remote

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val usuario: String,
    val cliente: ClienteData,

)

class ClienteData(
    val codigo: String?,
    val nombre: String?,
    @SerializedName("id_documento") val idDocumento: Int?,
    @SerializedName("numero_dto") val numeroDto: Int?,
    @SerializedName("id_provincia") val idProvincia: Int?,
    @SerializedName("id_localidad") val idLocalidad: Int?,
    @SerializedName("codigo_postal") val codigoPostal: String?,
    val domicilio: String?,
    val telefono: String?,
    val mail: String?,
    val ctacte: String?
) {

}
