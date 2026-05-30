package com.example.cinelog

import com.example.cinelog.data.model.MovieItem
import com.example.cinelog.data.repository.UserListRepository
import com.example.cinelog.domain.model.ListType
import com.example.cinelog.ui.lists.UserListViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserListViewModelTest {

    private lateinit var viewModel: UserListViewModel
    private lateinit var userListRepository: UserListRepository
    private val testDispatcher = StandardTestDispatcher()

    private val peliculaFalsa = MovieItem(
        movieId = 1,
        titulo = "Inception",
        posterPath = "/poster.jpg",
        mediaType = "movie"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userListRepository = mockk()

        // Respuestas por defecto vacías
        coEvery { userListRepository.getMoviesFromList(ListType.WATCHLIST) } returns Result.success(emptyList())
        coEvery { userListRepository.getMoviesFromList(ListType.FAVORITAS) } returns Result.success(emptyList())
        coEvery { userListRepository.getMoviesFromList(ListType.YA_VISTO) } returns Result.success(emptyList())

        viewModel = UserListViewModel(userListRepository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // 1. Carga inicial devuelve listas vacías
    @Test
    fun `loadAllLists loads empty lists initially`() {
        val state = viewModel.uiState.value
        assertTrue(state.watchlist.isEmpty())
        assertTrue(state.favoritas.isEmpty())
        assertTrue(state.yaVisto.isEmpty())
    }

    // 2. Agregar a watchlist actualiza el estado
    @Test
    fun `addToList adds movie to watchlist`() = runTest {
        coEvery {
            userListRepository.addMovieToList(peliculaFalsa, ListType.WATCHLIST)
        } returns Result.success(true)

        coEvery {
            userListRepository.getMoviesFromList(ListType.WATCHLIST)
        } returns Result.success(listOf(peliculaFalsa))

        viewModel.addToList(peliculaFalsa, ListType.WATCHLIST)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.watchlist.size)
        assertEquals("Inception", viewModel.uiState.value.watchlist[0].titulo)
    }

    // 3. Agregar a favoritas también agrega a yaVisto
    @Test
    fun `addToList favoritas also adds to yaVisto`() = runTest {
        coEvery {
            userListRepository.addMovieToList(peliculaFalsa, ListType.FAVORITAS)
        } returns Result.success(true)

        coEvery {
            userListRepository.addMovieToList(peliculaFalsa, ListType.YA_VISTO)
        } returns Result.success(true)

        coEvery {
            userListRepository.getMoviesFromList(ListType.FAVORITAS)
        } returns Result.success(listOf(peliculaFalsa))

        coEvery {
            userListRepository.getMoviesFromList(ListType.YA_VISTO)
        } returns Result.success(listOf(peliculaFalsa))

        viewModel.addToList(peliculaFalsa, ListType.FAVORITAS)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.favoritas.size)
        assertEquals(1, viewModel.uiState.value.yaVisto.size)
    }

    // 4. Eliminar de lista actualiza el estado
    @Test
    fun `removeFromList removes movie from watchlist`() = runTest {
        coEvery {
            userListRepository.removeMovieFromList(peliculaFalsa, ListType.WATCHLIST)
        } returns Result.success(true)

        coEvery {
            userListRepository.getMoviesFromList(ListType.WATCHLIST)
        } returns Result.success(emptyList())

        viewModel.removeFromList(peliculaFalsa, ListType.WATCHLIST)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.watchlist.isEmpty())
    }

    // 5. Error al cargar listas se refleja en el estado
    @Test
    fun `addToList shows error when repository fails`() = runTest {
        coEvery {
            userListRepository.addMovieToList(peliculaFalsa, ListType.WATCHLIST)
        } returns Result.failure(Exception("Sin conexión"))

        viewModel.addToList(peliculaFalsa, ListType.WATCHLIST)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Sin conexión", viewModel.uiState.value.errorMessage)
    }
}