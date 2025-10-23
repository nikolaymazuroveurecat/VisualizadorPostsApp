package com.itb.visualizadorpostsapp

import android.app.Application
import com.itb.visualizadorpostsapp.di.appModule
import com.itb.visualizadorpostsapp.di.databaseModule
import com.itb.visualizadorpostsapp.di.networkModule
import com.itb.visualizadorpostsapp.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Application класс для инициализации Koin
 */
class PostViewerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Инициализация Koin
        startKoin {
            // Логирование Koin (для отладки)
            androidLogger(Level.DEBUG)

            // Android контекст для Koin
            androidContext(this@PostViewerApplication)

            // Загрузка всех модулей
            modules(
                listOf(
                    networkModule,
                    databaseModule,
                    repositoryModule,
                    appModule
                )
            )
        }
    }
}