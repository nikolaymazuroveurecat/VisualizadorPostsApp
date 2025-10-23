package com.itb.visualizadorpostsapp.data.remote

import com.itb.visualizadorpostsapp.data.remote.api.PostApi
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

/**
 * Test unitario para PostApi usando MockEngine
 */
class PostApiTest {

    /**
     * Test para obtener con éxito la lista de posts
     */
    @Test
    fun `getAllPosts should return list of posts when API responds successfully`() = runTest {
        // Given: Mock de respuesta JSON
        val mockJsonResponse = """
            [
                {
                    "userId": 1,
                    "id": 1,
                    "title": "Test Post 1",
                    "body": "This is test post 1"
                },
                {
                    "userId": 1,
                    "id": 2,
                    "title": "Test Post 2",
                    "body": "This is test post 2"
                }
            ]
        """.trimIndent()

        // Mock Engine
        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath == "/posts" && request.method == HttpMethod.Get -> {
                    respond(
                        content = ByteReadChannel(mockJsonResponse),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                else -> error("Unhandled ${request.url.encodedPath}")
            }
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }

        val postApi = PostApi(httpClient)

        // When: llamamos a la API
        val result = postApi.getAllPosts()

        // Then: verificamos el resultado
        assertEquals(2, result.size)
        assertEquals(1, result[0].id)
        assertEquals("Test Post 1", result[0].title)
        assertEquals("This is test post 1", result[0].body)
        assertEquals(2, result[1].id)
    }

    /**
     * Test para obtener con éxito un post por ID
     */
    @Test
    fun `getPostById should return single post when API responds successfully`() = runTest {
        // Given
        val postId = 1
        val mockJsonResponse = """
            {
                "userId": 1,
                "id": 1,
                "title": "Test Post",
                "body": "This is a test post"
            }
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath == "/posts/$postId" && request.method == HttpMethod.Get -> {
                    respond(
                        content = ByteReadChannel(mockJsonResponse),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                else -> error("Unhandled ${request.url.encodedPath}")
            }
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }

        val postApi = PostApi(httpClient)

        // When
        val result = postApi.getPostById(postId)

        // Then
        assertEquals(1, result.id)
        assertEquals("Test Post", result.title)
        assertEquals("This is a test post", result.body)
        assertEquals(1, result.userId)
    }

    /**
     * Test para manejar el error 404
     */
    @Test(expected = Exception::class)
    fun `getPostById should throw exception when API returns 404`() = runTest {
        // Given
        val mockEngine = MockEngine { request ->
            respond(
                content = ByteReadChannel("Not Found"),
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, "text/plain")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json()
            }
        }

        val postApi = PostApi(httpClient)

        // When & Then: debería lanzar una excepción
        postApi.getPostById(999)
    }
}