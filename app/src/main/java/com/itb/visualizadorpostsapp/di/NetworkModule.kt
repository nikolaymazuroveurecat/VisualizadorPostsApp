package com.itb.visualizadorpostsapp.di

import com.itb.visualizadorpostsapp.data.remote.api.PostApi
import com.itb.visualizadorpostsapp.data.remote.client.KtorClient
import io.ktor.client.*
import org.koin.dsl.module

/**
 * Módulo Koin para dependencias de red
 */
val networkModule = module {

    // Proporciona un singleton HttpClient
    single<HttpClient> {
        KtorClient.create()
    }

    // Proporciona un singleton PostApi
    single<PostApi> {
        PostApi(client = get())
    }
}
