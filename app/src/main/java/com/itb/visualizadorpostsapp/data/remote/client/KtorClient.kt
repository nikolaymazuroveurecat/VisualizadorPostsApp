package com.itb.visualizadorpostsapp.data.remote.client

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Singleton para crear y configurar el cliente Ktor HTTP
 */
object KtorClient {

    /**
     * Crea y configura HttpClient con soporte para JSON
     */
    fun create(): HttpClient {
        return HttpClient(Android) {
            // Establecer tiempos de espera
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 15000
                socketTimeoutMillis = 15000
            }

            // Configuración de Content Negotiation para trabajar con JSON
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            // Registro de solicitudes (útil para la depuración)
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.BODY
            }

            // Configuración predeterminada para todas las solicitudes
            defaultRequest {
                url("https://jsonplaceholder.typicode.com/")
            }
        }
    }
}