package com.itb.visualizadorpostsapp.ui.screens.postlist

import com.itb.visualizadorpostsapp.domain.model.Post

/**
 * Estado de la UI para la pantalla de la lista de posts
 */
sealed class PostListUiState {
    // Estado de carga inicial
    object Loading : PostListUiState()

    // Carga de datos exitosa
    data class Success(
        val posts: List<Post>,
        val isOffline: Boolean = false
    ) : PostListUiState()

    // Error de carga (pero puede haber datos locales)
    data class Error(
        val message: String,
        val posts: List<Post> = emptyList()
    ) : PostListUiState()
}