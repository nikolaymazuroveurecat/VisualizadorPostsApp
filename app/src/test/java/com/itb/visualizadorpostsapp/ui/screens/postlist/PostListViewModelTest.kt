package com.itb.visualizadorpostsapp.ui.screens.postlist

import com.itb.visualizadorpostsapp.domain.model.Post
import com.itb.visualizadorpostsapp.domain.repository.PostRepository
import com.itb.visualizadorpostsapp.utils.TestCoroutineRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Test unitario para PostListViewModel
 * Comprueba la gestión de los estados de la UI
 */
@ExperimentalCoroutinesApi
class PostListViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var repository: PostRepository
    private lateinit var viewModel: PostListViewModel

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    /**
     * Test: estado inicial Loading
     */
    @Test
    fun `initial state should be Loading`() = runTest {
        // Given
        every { repository.getPosts() } returns flowOf(emptyList())
        coEvery { repository.refreshPosts() } just Runs

        // When
        viewModel = PostListViewModel(repository)

        // Then: estado inicial Loading
        assertTrue(viewModel.uiState.value is PostListUiState.Loading)
    }

    /**
     * Test: estado Success con datos del repositorio
     */
    @Test
    fun `loadPosts should emit Success state when repository returns data`() = runTest {
        // Given: Mock de repositorio con datos
        val mockPosts = listOf(
            Post(id = 1, userId = 1, title = "Post 1", body = "Body 1"),
            Post(id = 2, userId = 1, title = "Post 2", body = "Body 2")
        )

        every { repository.getPosts() } returns flowOf(mockPosts)
        coEvery { repository.refreshPosts() } just Runs

        // When: inicializamos el ViewModel
        viewModel = PostListViewModel(repository)
        advanceUntilIdle() // Esperamos a que se completen las corutinas

        // Then: el estado debe ser Success con datos
        val state = viewModel.uiState.value
        assertTrue(state is PostListUiState.Success)

        val successState = state as PostListUiState.Success
        assertEquals(2, successState.posts.size)
        assertEquals("Post 1", successState.posts[0].title)
        assertFalse(successState.isOffline)
    }

    /**
     * Test: estado Loading cuando no hay datos
     */
    @Test
    fun `loadPosts should emit Loading state when repository returns empty list`() = runTest {
        // Given: lista vacía
        every { repository.getPosts() } returns flowOf(emptyList())
        coEvery { repository.refreshPosts() } just Runs

        // When
        viewModel = PostListViewModel(repository)
        advanceUntilIdle()

        // Then: estado Loading
        assertTrue(viewModel.uiState.value is PostListUiState.Loading)
    }

    /**
     * Test: refreshPosts llama a la actualización del repositorio
     */
    @Test
    fun `refreshPosts should call repository refreshPosts`() = runTest {
        // Given
        val mockPosts = listOf(
            Post(id = 1, userId = 1, title = "Post", body = "Body")
        )

        every { repository.getPosts() } returns flowOf(mockPosts)
        coEvery { repository.refreshPosts() } just Runs

        viewModel = PostListViewModel(repository)
        advanceUntilIdle()

        // When: llamamos a refresh
        viewModel.refreshPosts()
        advanceUntilIdle()

        // Then: el repositorio se llama dos veces (init + refresh manual)
        coVerify(exactly = 2) { repository.refreshPosts() }
    }

    /**
     * Test: modo offline en caso de error de refresh
     */
    @Test
    fun `refreshPosts should set isOffline true when refresh fails`() = runTest {
        // Given: datos locales exitosos
        val mockPosts = listOf(
            Post(id = 1, userId = 1, title = "Cached Post", body = "Cached Body")
        )

        every { repository.getPosts() } returns flowOf(mockPosts)
        coEvery { repository.refreshPosts() } throws Exception("Network error")

        // When
        viewModel = PostListViewModel(repository)
        advanceUntilIdle()

        // Then: Success con isOffline = true
        val state = viewModel.uiState.value
        assertTrue(state is PostListUiState.Success)

        val successState = state as PostListUiState.Success
        assertEquals(1, successState.posts.size)
        assertTrue(successState.isOffline)
    }

    /**
     * Test: llamada repetida a loadPosts
     */
    @Test
    fun `loadPosts should reload data when called again`() = runTest {
        // Given
        val mockPosts = listOf(
            Post(id = 1, userId = 1, title = "Post", body = "Body")
        )

        every { repository.getPosts() } returns flowOf(mockPosts)
        coEvery { repository.refreshPosts() } just Runs

        viewModel = PostListViewModel(repository)
        advanceUntilIdle()

        // When: llamada repetida a loadPosts
        viewModel.loadPosts()
        advanceUntilIdle()

        // Then: el repositorio se ha llamado varias veces
        verify(atLeast = 2) { repository.getPosts() }
        coVerify(atLeast = 2) { repository.refreshPosts() }
    }
}
