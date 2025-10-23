package com.itb.visualizadorpostsapp.domain.model

/**
 * Modelo de datos para una publicación (Capa de dominio)
 */
data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)