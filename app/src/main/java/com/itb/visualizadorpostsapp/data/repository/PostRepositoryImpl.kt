package com.itb.visualizadorpostsapp.data.repository

import com.itb.visualizadorpostsapp.data.local.dao.PostDao
import com.itb.visualizadorpostsapp.data.local.entity.PostEntity
import com.itb.visualizadorpostsapp.data.remote.api.PostApi
import com.itb.visualizadorpostsapp.data.remote.dto.PostDto
import com.itb.visualizadorpostsapp.domain.model.Post
import com.itb.visualizadorpostsapp.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación de PostRepository
 * Coordina el trabajo entre las fuentes de datos locales y de red
 */
class PostRepositoryImpl(
    private val postDao: PostDao,
    private val postApi: PostApi
) : PostRepository {

    /**
     * Obtiene los posts de la BD de Room y los convierte en modelos de dominio
     */
    override fun getPosts(): Flow<List<Post>> {
        return postDao.getAllPosts().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    /**
     * Obtiene un post por ID de la BD de Room
     */
    override fun getPostById(postId: Int): Flow<Post?> {
        return postDao.getPostById(postId).map { entity ->
            entity?.toDomainModel()
        }
    }

    /**
     * Actualiza todos los posts:
     * 1. Obtiene datos de la API
     * 2. Convierte DTO en Entity
     * 3. Guarda en la BD de Room
     */
    override suspend fun refreshPosts() {
        try {
            val postsDto = postApi.getAllPosts()
            val entities = postsDto.map { it.toEntity() }
            postDao.insertPosts(entities)
        } catch (e: Exception) {
            // Registramos el error, pero no lo propagamos
            // La UI utilizará los datos locales
            e.printStackTrace()
            throw e
        }
    }

    /**
     * Actualiza un post específico por ID desde la red
     */
    override suspend fun refreshPostById(postId: Int) {
        try {
            val postDto = postApi.getPostById(postId)
            val entity = postDto.toEntity()
            postDao.insertPost(entity)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    // Mapper: PostEntity -> Post (Modelo de Dominio)
    private fun PostEntity.toDomainModel(): Post {
        return Post(
            id = this.id,
            userId = this.userId,
            title = this.title,
            body = this.body
        )
    }

    // Mapper: PostDto -> PostEntity
    private fun PostDto.toEntity(): PostEntity {
        return PostEntity(
            id = this.id,
            userId = this.userId,
            title = this.title,
            body = this.body
        )
    }
}
