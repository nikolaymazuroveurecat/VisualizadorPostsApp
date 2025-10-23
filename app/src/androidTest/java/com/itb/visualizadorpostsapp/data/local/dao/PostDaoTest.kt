package com.itb.visualizadorpostsapp.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.itb.visualizadorpostsapp.data.local.database.AppDatabase
import com.itb.visualizadorpostsapp.data.local.entity.PostEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Test instrumentado para PostDao
 * Utiliza una base de datos en memoria para aislar los tests
 */
@RunWith(AndroidJUnit4::class)
class PostDaoTest {

    private lateinit var postDao: PostDao
    private lateinit var database: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Creamos una base de datos en memoria
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries() // ¡Solo para tests!
            .build()

        postDao = database.postDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    /**
     * Test: insertar y leer un post
     */
    @Test
    fun insertPost_andReadById() = runTest {
        // Given: creamos un post
        val post = PostEntity(
            id = 1,
            userId = 1,
            title = "Test Post",
            body = "Test Body"
        )

        // When: insertamos el post
        postDao.insertPost(post)

        // Then: leemos el post por ID
        val result = postDao.getPostById(1).first()

        assertNotNull(result)
        assertEquals(post.id, result?.id)
        assertEquals(post.title, result?.title)
        assertEquals(post.body, result?.body)
    }

    /**
     * Test: insertar una lista de posts
     */
    @Test
    fun insertMultiplePosts_andReadAll() = runTest {
        // Given: lista de posts
        val posts = listOf(
            PostEntity(id = 1, userId = 1, title = "Post 1", body = "Body 1"),
            PostEntity(id = 2, userId = 1, title = "Post 2", body = "Body 2"),
            PostEntity(id = 3, userId = 2, title = "Post 3", body = "Body 3")
        )

        // When: insertamos los posts
        postDao.insertPosts(posts)

        // Then: leemos todos los posts
        val result = postDao.getAllPosts().first()

        assertEquals(3, result.size)
        assertEquals("Post 1", result[0].title)
        assertEquals("Post 2", result[1].title)
        assertEquals("Post 3", result[2].title)
    }

    /**
     * Test: reemplazar un post en caso de conflicto (OnConflictStrategy.REPLACE)
     */
    @Test
    fun insertPost_withConflict_shouldReplace() = runTest {
        // Given: post original
        val originalPost = PostEntity(
            id = 1,
            userId = 1,
            title = "Original Title",
            body = "Original Body"
        )

        postDao.insertPost(originalPost)

        // When: insertamos un post con el mismo ID pero con datos diferentes
        val updatedPost = PostEntity(
            id = 1,
            userId = 1,
            title = "Updated Title",
            body = "Updated Body"
        )

        postDao.insertPost(updatedPost)

        // Then: el post debe ser reemplazado
        val result = postDao.getPostById(1).first()

        assertNotNull(result)
        assertEquals("Updated Title", result?.title)
        assertEquals("Updated Body", result?.body)

        // Comprobamos que solo hay 1 post
        val allPosts = postDao.getAllPosts().first()
        assertEquals(1, allPosts.size)
    }

    /**
     * Test: actualizar un post
     */
    @Test
    fun updatePost_shouldModifyExisting() = runTest {
        // Given: insertamos un post
        val post = PostEntity(
            id = 1,
            userId = 1,
            title = "Original",
            body = "Original Body"
        )

        postDao.insertPost(post)

        // When: actualizamos el post
        val updatedPost = post.copy(
            title = "Updated",
            body = "Updated Body"
        )

        postDao.updatePost(updatedPost)

        // Then: comprobamos la actualización
        val result = postDao.getPostById(1).first()

        assertEquals("Updated", result?.title)
        assertEquals("Updated Body", result?.body)
    }

    /**
     * Test: eliminar todos los posts
     */
    @Test
    fun deleteAllPosts_shouldClearDatabase() = runTest {
        // Given: insertamos varios posts
        val posts = listOf(
            PostEntity(id = 1, userId = 1, title = "Post 1", body = "Body 1"),
            PostEntity(id = 2, userId = 1, title = "Post 2", body = "Body 2")
        )

        postDao.insertPosts(posts)

        // Comprobamos que los posts existen
        var result = postDao.getAllPosts().first()
        assertEquals(2, result.size)

        // When: eliminamos todos los posts
        postDao.deleteAllPosts()

        // Then: la BD debe estar vacía
        result = postDao.getAllPosts().first()
        assertTrue(result.isEmpty())
    }

    /**
     * Test: leer un post inexistente devuelve null
     */
    @Test
    fun getPostById_nonExistent_returnsNull() = runTest {
        // When: intentamos leer un post inexistente
        val result = postDao.getPostById(999).first()

        // Then: debe devolver null
        assertNull(result)
    }

    /**
     * Test: una BD vacía devuelve una lista vacía
     */
    @Test
    fun getAllPosts_emptyDatabase_returnsEmptyList() = runTest {
        // When: leemos de una BD vacía
        val result = postDao.getAllPosts().first()

        // Then: la lista está vacía
        assertTrue(result.isEmpty())
    }

    /**
     * Test: los posts están ordenados por ID (ORDER BY id ASC)
     */
    @Test
    fun getAllPosts_shouldBeOrderedById() = runTest {
        // Given: insertamos los posts en orden aleatorio
        val posts = listOf(
            PostEntity(id = 5, userId = 1, title = "Post 5", body = "Body 5"),
            PostEntity(id = 2, userId = 1, title = "Post 2", body = "Body 2"),
            PostEntity(id = 8, userId = 2, title = "Post 8", body = "Body 8"),
            PostEntity(id = 1, userId = 1, title = "Post 1", body = "Body 1")
        )

        postDao.insertPosts(posts)

        // When: leemos todos los posts
        val result = postDao.getAllPosts().first()

        // Then: deben estar ordenados por ID
        assertEquals(4, result.size)
        assertEquals(1, result[0].id)
        assertEquals(2, result[1].id)
        assertEquals(5, result[2].id)
        assertEquals(8, result[3].id)
    }
}
