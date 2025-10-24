package com.itb.visualizadorpostsapp.ui.screens.postlist

import com.itb.visualizadorpostsapp.domain.model.Post

/**
 * Estado de la UI para la pantalla de la lista de posts
 */
sealed class PostListUiState {
    object Loading : PostListUiState()

    data class Success(
        val posts: List<Post>,
        val isOffline: Boolean = false
    ) : PostListUiState()

    data class Error(
        val message: String,
    ) : PostListUiState()
}