package com.itb.visualizadorpostsapp.domain.repository

import com.itb.visualizadorpostsapp.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getPosts(): Flow<List<Post>>
    suspend fun refreshPosts()
    fun getPostById(postId: Int): Flow<Post?>
    suspend fun refreshPostById(postId: Int)
}