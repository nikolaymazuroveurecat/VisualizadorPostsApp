package com.itb.visualizadorpostsapp.data.repository

import com.itb.visualizadorpostsapp.data.local.dao.PostDao
import com.itb.visualizadorpostsapp.data.local.entity.PostEntity
import com.itb.visualizadorpostsapp.data.remote.api.PostApi
import com.itb.visualizadorpostsapp.data.remote.dto.PostDto
import com.itb.visualizadorpostsapp.domain.repository.PostRepository
import com.itb.visualizadorpostsapp.utils.TestCoroutineRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Test unitario para PostRepositoryImpl
 * Comprueba la lógica de coordinación entre Room y Ktor
 */
@ExperimentalCoroutinesApi
class PostRepositoryImplTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    // Mocks
    private lateinit var postDao: PostDao
    private lateinit var postApi: PostApi
    private lateinit var repository: PostRepository

    @Before
    fun setup() {
        postDao = mockk()
        postApi = mockk()
        repository = PostRepositoryImpl(postDao, postApi)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    /**
     * Test: getPosts devuelve datos de Room
     */
    @Test
    fun `getPosts should return posts from Room database`() = runTest {
        // Given: Mock de datos de Room
        val mockEntities = listOf(
            PostEntity(id = 1, userId = 1, title = "Post 1", body = "Body 1"),
            PostEntity(id = 2, userId = 1, title = "Post 2", body = "Body 2")
        )

        every { postDao.getAllPosts() } returns flowOf(mockEntities)

        // When: obtenemos los posts
        val result = repository.getPosts().first()

        // Then: comprobamos que los datos se convierten en modelos de Dominio
        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals("Post 1", result[0].title)
        assertEquals("Body 1", result[0].body)
        verify(exactly = 1) { postDao.getAllPosts() }
    }

    /**
     * Test: refreshPosts carga datos de la API y los guarda en Room
     */
    @Test
    fun `refreshPosts should fetch from API and save to Room`() = runTest {
        // Given: Mock de la respuesta de la API
        val mockDtos = listOf(
            PostDto(id = 1, userId = 1, title = "API Post 1", body = "API Body 1"),
            PostDto(id = 2, userId = 1, title = "API Post 2", body = "API Body 2")
        )

        coEvery { postApi.getAllPosts() } returns mockDtos
        coEvery { postDao.insertPosts(any()) } just Runs

        // When: actualizamos los posts
        repository.refreshPosts()

        // Then: comprobamos que se llamó a la API y se guardaron los datos
        coVerify(exactly = 1) { postApi.getAllPosts() }
        coVerify(exactly = 1) {
            postDao.insertPosts(match { entities ->
                entities.size == 2 &&
                        entities[0].title == "API Post 1" &&
                        entities[1].title == "API Post 2"
            })
        }
    }

    /**
     * Test: refreshPosts lanza una excepción en caso de error de red
     */
    @Test(expected = Exception::class)
    fun `refreshPosts should throw exception when API fails`() = runTest {
        // Given: La API lanza una excepción
        coEvery { postApi.getAllPosts() } throws Exception("Network error")

        // When & Then: debería lanzar una excepción
        repository.refreshPosts()
    }

    /**
     * Test: getPostById devuelve un post de Room
     */
    @Test
    fun `getPostById should return post from Room database`() = runTest {
        // Given
        val postId = 1
        val mockEntity = PostEntity(
            id = postId,
            userId = 1,
            title = "Single Post",
            body = "Single Body"
        )

        every { postDao.getPostById(postId) } returns flowOf(mockEntity)

        // When
        val result = repository.getPostById(postId).first()

        // Then
        assertNotNull(result)
        assertEquals(postId, result?.id)
        assertEquals("Single Post", result?.title)
        verify(exactly = 1) { postDao.getPostById(postId) }
    }

    /**
     * Test: getPostById devuelve null si no se encuentra el post
     */
    @Test
    fun `getPostById should return null when post not found`() = runTest {
        // Given
        val postId = 999
        every { postDao.getPostById(postId) } returns flowOf(null)

        // When
        val result = repository.getPostById(postId).first()

        // Then
        assertNull(result)
        verify(exactly = 1) { postDao.getPostById(postId) }
    }

    /**
     * Test: refreshPostById actualiza un post específico
     */
    @Test
    fun `refreshPostById should fetch single post and save to Room`() = runTest {
        // Given
        val postId = 1
        val mockDto = PostDto(
            id = postId,
            userId = 1,
            title = "Updated Post",
            body = "Updated Body"
        )

        coEvery { postApi.getPostById(postId) } returns mockDto
        coEvery { postDao.insertPost(any()) } just Runs

        // When
        repository.refreshPostById(postId)

        // Then
        coVerify(exactly = 1) { postApi.getPostById(postId) }
        coVerify(exactly = 1) {
            postDao.insertPost(match { entity ->
                entity.id == postId && entity.title == "Updated Post"
            })
        }
    }
}
