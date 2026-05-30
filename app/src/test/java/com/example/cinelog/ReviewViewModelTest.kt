package com.example.cinelog

import com.example.cinelog.data.repository.ReviewRepository
import com.example.cinelog.data.repository.UserListRepository
import com.example.cinelog.domain.model.ListType
import com.example.cinelog.domain.model.Review
import com.example.cinelog.ui.review.ReviewViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReviewViewModelTest {

    private lateinit var viewModel: ReviewViewModel
    private lateinit var reviewRepository: ReviewRepository
    private lateinit var userListRepository: UserListRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        reviewRepository = mockk()
        userListRepository = mockk()
        viewModel = ReviewViewModel(reviewRepository, userListRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // 1. Error cuando calificacion es 0
    @Test
    fun `saveReview shows error when calificacion is 0`() {
        viewModel.onReseñaChange("Buena película")
        viewModel.saveReview(1, "movie", "Inception", "/poster.jpg")

        val state = viewModel.uiState.value
        assertEquals("Selecciona una calificación", state.errorMessage)
    }

    // 2. Error cuando reseña está vacía
    @Test
    fun `saveReview shows error when reseña is blank`() {
        viewModel.onCalificacionChange(5)
        viewModel.saveReview(1, "movie", "Inception", "/poster.jpg")

        val state = viewModel.uiState.value
        assertEquals("Escribe una reseña", state.errorMessage)
    }

    // 3. Guardado exitoso marca isSaveSuccessful y muestra toast
    @Test
    fun `saveReview sets isSaveSuccessful and toast on success`() = runTest {
        coEvery { reviewRepository.saveReview(any()) } returns Result.success(true)
        coEvery {
            userListRepository.addMovieToList(any(), ListType.YA_VISTO)
        } returns Result.success(true)

        viewModel.onCalificacionChange(5)
        viewModel.onReseñaChange("Excelente película")
        viewModel.saveReview(1, "movie", "Inception", "/poster.jpg")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isSaveSuccessful)
        assertEquals("Reseña guardada con éxito", state.successToastMessage)
        assertNull(state.errorMessage)
    }

    // 4. Al guardar una reseña nueva agrega la pelicula a yaVisto
    @Test
    fun `saveReview adds movie to yaVisto on success`() = runTest {
        coEvery { reviewRepository.saveReview(any()) } returns Result.success(true)
        coEvery {
            userListRepository.addMovieToList(any(), ListType.YA_VISTO)
        } returns Result.success(true)

        viewModel.onCalificacionChange(4)
        viewModel.onReseñaChange("Muy buena")
        viewModel.saveReview(1, "movie", "Inception", "/poster.jpg")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { userListRepository.addMovieToList(any(), ListType.YA_VISTO) }
    }

    // 5. Al editar una reseña existente el toast dice "Reseña actualizada"
    @Test
    fun `saveReview shows actualizada toast when editing existing review`() = runTest {
        coEvery { reviewRepository.saveReview(any()) } returns Result.success(true)
        coEvery {
            userListRepository.addMovieToList(any(), ListType.YA_VISTO)
        } returns Result.success(true)
        coEvery {
            reviewRepository.getReview(any(), any())
        } returns Result.success(
            Review(
                movieId = 1,
                titulo = "Inception",
                posterPath = "/poster.jpg",
                calificacion = 3,
                reseña = "Buena",
                etiquetas = "",
                mediaType = "movie"
            )
        )

        // Simular que ya existe una reseña
        viewModel.loadReviewForMovie(1, "movie")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onCalificacionChange(5)
        viewModel.onReseñaChange("Actualizada")
        viewModel.saveReview(1, "movie", "Inception", "/poster.jpg")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Reseña actualizada", viewModel.uiState.value.successToastMessage)
    }

    // 6. Error del repositorio se muestra en el estado
    @Test
    fun `saveReview shows error when repository fails`() = runTest {
        coEvery {
            reviewRepository.saveReview(any())
        } returns Result.failure(Exception("Error al guardar"))

        viewModel.onCalificacionChange(5)
        viewModel.onReseñaChange("Buena película")
        viewModel.saveReview(1, "movie", "Inception", "/poster.jpg")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Error al guardar", state.errorMessage)
        assertTrue(!state.isSaveSuccessful)
    }

    // 7. onCalificacionChange limpia el error
    @Test
    fun `onCalificacionChange clears errorMessage`() {
        viewModel.saveReview(1, "movie", "Inception", "/poster.jpg") // genera error
        viewModel.onCalificacionChange(5) // limpia error

        assertNull(viewModel.uiState.value.errorMessage)
    }

    // 8. onReseñaChange limpia el error
    @Test
    fun `onReseñaChange clears errorMessage`() {
        viewModel.onCalificacionChange(5)
        viewModel.saveReview(1, "movie", "Inception", "/poster.jpg") // genera error reseña vacía
        viewModel.onReseñaChange("Nueva reseña") // limpia error

        assertNull(viewModel.uiState.value.errorMessage)
    }
}