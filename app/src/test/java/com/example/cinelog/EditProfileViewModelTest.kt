package com.example.cinelog

import com.example.cinelog.data.repository.AuthRepository
import com.example.cinelog.ui.editProfile.EditProfileViewModel
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditProfileViewModelTest {

    private lateinit var viewModel: EditProfileViewModel
    private lateinit var authRepository: AuthRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        viewModel = EditProfileViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // 1. Carga correcta de datos del usuario
    @Test
    fun `loadUser sets nombre edad and email in state`() {
        viewModel.loadUser("Juan", "25", "juan@email.com")

        val state = viewModel.uiState.value
        assertEquals("Juan", state.nombre)
        assertEquals("25", state.edad)
        assertEquals("juan@email.com", state.email)
    }

    // 2. Error cuando nombre está vacío
    @Test
    fun `onSaveClick shows error when nombre is blank`() {
        viewModel.loadUser("", "25", "juan@email.com")
        viewModel.onSaveClick()

        val state = viewModel.uiState.value
        assertNotNull(state.errorMessage)
        assertEquals("Nombre, edad y correo son obligatorios", state.errorMessage)
    }

    // 3. Error cuando edad no es un número válido
    @Test
    fun `onSaveClick shows error when edad is not a number`() {
        viewModel.loadUser("Juan", "abc", "juan@email.com")
        viewModel.onSaveClick()

        val state = viewModel.uiState.value
        assertNotNull(state.errorMessage)
        assertEquals("Ingresa una edad válida", state.errorMessage)
    }

    // 4. Error cuando email es inválido
    @Test
    fun `onSaveClick shows error when email is invalid`() {
        viewModel.loadUser("Juan", "25", "correo-invalido")
        viewModel.onSaveClick()

        val state = viewModel.uiState.value
        assertNotNull(state.errorMessage)
        assertEquals("Ingresa un correo válido", state.errorMessage)
    }

    // 5. Guardado exitoso llama al repositorio y marca isSaveSuccessful
    @Test
    fun `onSaveClick sets isSaveSuccessful true on success`() = runTest {
        coEvery {
            authRepository.updateProfile("Juan", 25, "juan@email.com")
        } returns Result.success(true)

        viewModel.loadUser("Juan", "25", "juan@email.com")
        viewModel.onSaveClick()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(true, state.isSaveSuccessful)
        assertNull(state.errorMessage)
    }

    // 6. Error del repositorio se muestra en el estado
    @Test
    fun `onSaveClick shows error when repository fails`() = runTest {
        coEvery {
            authRepository.updateProfile(any(), any(), any())
        } returns Result.failure(Exception("Error de red"))

        viewModel.loadUser("Juan", "25", "juan@email.com")
        viewModel.onSaveClick()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Error de red", state.errorMessage)
        assertEquals(false, state.isSaveSuccessful)
    }

    // 7. onSaveHandled resetea isSaveSuccessful
    @Test
    fun `onSaveHandled resets isSaveSuccessful to false`() = runTest {
        coEvery {
            authRepository.updateProfile("Juan", 25, "juan@email.com")
        } returns Result.success(true)

        viewModel.loadUser("Juan", "25", "juan@email.com")
        viewModel.onSaveClick()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSaveHandled()
        assertEquals(false, viewModel.uiState.value.isSaveSuccessful)
    }

    // 8. Cambios de campos limpian el error
    @Test
    fun `onNombreChange clears errorMessage`() {
        viewModel.loadUser("", "25", "juan@email.com")
        viewModel.onSaveClick() // genera error
        viewModel.onNombreChange("Juan") // limpia error

        assertNull(viewModel.uiState.value.errorMessage)
    }
}