package com.itb.visualizadorpostsapp.di

import com.itb.visualizadorpostsapp.ui.screens.postdetail.PostDetailViewModel
import com.itb.visualizadorpostsapp.ui.screens.postlist.PostListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Módulo Koin principal para ViewModels
 */
val appModule = module {

    // Proporciona PostListViewModel
    viewModel {
        PostListViewModel(repository = get())
    }

    // Proporciona PostDetailViewModel con el parámetro postId
    viewModel { parameters ->
        PostDetailViewModel(
            repository = get(),
            postId = parameters.get()
        )
    }
}