package com.itb.visualizadorpostsapp.ui.screens.postdetail

import com.itb.visualizadorpostsapp.domain.model.Post

/**
 * Estado de la UI para la pantalla de detalle del post
 */
sealed class PostDetailUiState {
    object Loading : PostDetailUiState()

    data class Success(
        val post: Post,
        val isOffline: Boolean = false
    ) : PostDetailUiState()

    data class Error(
        val message: String
    ) : PostDetailUiState()
}