package com.itb.visualizadorpostsapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO (Objeto de Transferencia de Datos) para un post de la API
 * Se utiliza para la serialización/deserialización de JSON
 */
@Serializable
data class PostDto(
    @SerialName("id")
    val id: Int,

    @SerialName("userId")
    val userId: Int,

    @SerialName("title")
    val title: String,

    @SerialName("body")
    val body: String
)
