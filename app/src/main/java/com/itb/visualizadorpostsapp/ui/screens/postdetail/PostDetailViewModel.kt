package com.itb.visualizadorpostsapp.ui.screens.postdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.visualizadorpostsapp.domain.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar los detalles del post
 */
class PostDetailViewModel(
    private val repository: PostRepository,
    private val postId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostDetailUiState>(PostDetailUiState.Loading)
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    init {
        loadPost()
    }

    /**
     * Carga los detalles del post
     * Primero desde Room, luego actualiza desde la red
     */
    private fun loadPost() {
        viewModelScope.launch {
            repository.getPostById(postId)
                .catch { error ->
                    _uiState.value = PostDetailUiState.Error(
                        message = error.message ?: "Unknown error occurred"
                    )
                }
                .collect { post ->
                    if (post == null) {
                        _uiState.value = PostDetailUiState.Loading
                    } else {
                        _uiState.value = PostDetailUiState.Success(post = post)
                    }
                }
        }

        // Actualizamos desde la red en segundo plano
        refreshPost()
    }

    /**
     * Actualiza los datos del post desde la red
     */
    fun refreshPost() {
        viewModelScope.launch {
            try {
                repository.refreshPostById(postId)
            } catch (e: Exception) {
                val currentState = _uiState.value
                if (currentState is PostDetailUiState.Success) {
                    _uiState.value = currentState.copy(isOffline = true)
                }
            }
        }
    }
}