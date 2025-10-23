package com.itb.visualizadorpostsapp.data.local.dao

import androidx.room.*
import androidx.room.Update
import com.itb.visualizadorpostsapp.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para trabajar con la tabla posts
 */
@Dao
interface PostDao {

    /**
     * Obtiene todos los posts de la BD como un Flow (flujo reactivo)
     * Se actualiza automáticamente cuando los datos cambian
     */
    @Query("SELECT * FROM posts ORDER BY id ASC")
    fun getAllPosts(): Flow<List<PostEntity>>

    /**
     * Obtiene un post por ID como un Flow
     * @param postId ID del post
     */
    @Query("SELECT * FROM posts WHERE id = :postId")
    fun getPostById(postId: Int): Flow<PostEntity?>

    /**
     * Inserta una lista de posts en la BD
     * En caso de conflicto, reemplaza los registros existentes
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    /**
     * Inserta un solo post en la BD
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    /**
     * Elimina todos los posts de la BD
     */
    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()

    /**
     * Actualiza un post en la BD
     */
    @Update
    suspend fun updatePost(post: PostEntity)
}
