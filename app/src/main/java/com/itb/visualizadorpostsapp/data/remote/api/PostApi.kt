package com.itb.visualizadorpostsapp.data.remote.api

import com.itb.visualizadorpostsapp.data.remote.dto.PostDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

/**
 * API para trabajar con posts a través de JSONPlaceholder
 */
class PostApi(private val client: HttpClient) {

    companion object {
        private const val POSTS_ENDPOINT = "posts"
    }

    /**
     * Obtiene todos los posts
     * @return Lista de todos los posts de la API
     */
    suspend fun getAllPosts(): List<PostDto> {
        return client.get(POSTS_ENDPOINT).body()
    }

    /**
     * Obtiene un post por su ID
     * @param postId ID del post
     * @return El post con el ID especificado
     */
    suspend fun getPostById(postId: Int): PostDto {
        return client.get("$POSTS_ENDPOINT/$postId").body()
    }
}