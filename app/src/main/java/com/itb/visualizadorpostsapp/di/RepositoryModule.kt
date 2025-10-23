package com.itb.visualizadorpostsapp.di

import com.itb.visualizadorpostsapp.data.repository.PostRepositoryImpl
import com.itb.visualizadorpostsapp.domain.repository.PostRepository
import org.koin.dsl.module

/**
 * Módulo Koin para repositorios
 */
val repositoryModule = module {

    // Proporciona un singleton PostRepository
    single<PostRepository> {
        PostRepositoryImpl(
            postDao = get(),
            postApi = get()
        )
    }
}
