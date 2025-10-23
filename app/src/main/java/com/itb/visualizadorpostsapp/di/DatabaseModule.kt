package com.itb.visualizadorpostsapp.di

import androidx.room.Room
import com.itb.visualizadorpostsapp.data.local.database.AppDatabase
import com.itb.visualizadorpostsapp.data.local.dao.PostDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Módulo Koin para dependencias de Room Database
 */
val databaseModule = module {

    // Proporciona un singleton AppDatabase
    single<AppDatabase> {
        Room.databaseBuilder(
            context = androidContext(),
            klass = AppDatabase::class.java,
            name = AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // Al cambiar el esquema, recrea la BD
            .build()
    }

    // Proporciona un singleton PostDao
    single<PostDao> {
        get<AppDatabase>().postDao()
    }
}
