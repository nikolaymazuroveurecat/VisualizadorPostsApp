package com.itb.visualizadorpostsapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.itb.visualizadorpostsapp.data.local.dao.PostDao
import com.itb.visualizadorpostsapp.data.local.entity.PostEntity

/**
 * Clase de la base de datos Room para la aplicación
 * Define todas las entidades y la versión de la BD
 */
@Database(
    entities = [PostEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Proporciona acceso a PostDao
     */
    abstract fun postDao(): PostDao

    companion object {
        const val DATABASE_NAME = "visualizador_posts_db"
    }
}
