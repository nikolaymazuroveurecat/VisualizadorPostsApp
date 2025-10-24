package com.itb.visualizadorpostsapp.ui.screens.postlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itb.visualizadorpostsapp.domain.repository.PostRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar la lista de posts
 */
class PostListViewModel(
    private val repository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostListUiState>(PostListUiState.Loading)
    val uiState: StateFlow<PostListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = PostListUiState.Loading
            delay(4000)
            loadPosts()
        }
    }

    /**
     * Carga los posts desde el repositorio
     * Primero se muestran los datos de Room, luego se actualizan desde la red
     */
    fun loadPosts() {
        viewModelScope.launch {
            // Nos suscribimos al Flow de Room
            repository.getPosts()
                .catch { error ->
                    _uiState.value = PostListUiState.Error(
                        message = error.message ?: "Unknown error occurred"
                    )
                }
                .collect { posts ->
                    if (posts.isEmpty()) {
                        _uiState.value = PostListUiState.Loading
                    } else {
                        _uiState.value = PostListUiState.Success(posts = posts)
                    }
                }
        }

        // Iniciamos la actualización desde la red en segundo plano
        refreshPosts()
    }

    /**
     * Actualiza los datos desde la red
     */
    fun refreshPosts() {
        viewModelScope.launch {
            try {
                repository.refreshPosts()
            } catch (e: Exception) {
                val currentState = _uiState.value
                if (currentState is PostListUiState.Success) {
                    _uiState.value = currentState.copy(isOffline = true)
                } else {
                    _uiState.value = PostListUiState.Error("Comprueba tu conexión a internet")
                }
            }
        }
    }
}