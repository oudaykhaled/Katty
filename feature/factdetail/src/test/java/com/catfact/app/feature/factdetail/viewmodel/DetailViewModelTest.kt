package com.catfact.app.feature.factdetail.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.catfact.app.core.domain.usecase.SaveNoteUseCase
import com.catfact.app.core.domain.usecase.ToggleBookmarkUseCase
import com.catfact.app.core.domain.repository.CatFactRepository
import com.catfact.app.core.telemetry.NoOpEventTracker
import com.catfact.app.core.testing.FakeCatFactRepository
import com.catfact.app.core.testing.TestFixtures
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import com.catfact.app.core.designsystem.util.UiText
import com.catfact.app.feature.factdetail.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeCatFactRepository
    private lateinit var savedStateHandle: SavedStateHandle

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeCatFactRepository()
        repository.setFacts(listOf(TestFixtures.createCatFact(id = "fact-1")))
        savedStateHandle = SavedStateHandle(mapOf(DetailViewModel.FACT_ID_KEY to "fact-1"))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        factId: String = "fact-1",
        repository: CatFactRepository = this.repository
    ) = DetailViewModel(
        savedStateHandle = savedStateHandle,
        factId = factId,
        repository = repository,
        toggleBookmarkUseCase = ToggleBookmarkUseCase(repository),
        saveNoteUseCase = SaveNoteUseCase(repository),
        eventTracker = NoOpEventTracker()
    )

    @Test
    fun `loads fact on init`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertNotNull(viewModel.state.value.fact)
        assertEquals("fact-1", viewModel.state.value.fact?.id)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `update note changes edited note and flags changes`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(DetailEvent.UpdateNote("new note"))

        assertEquals("new note", viewModel.state.value.editedNote)
        assertTrue(viewModel.state.value.hasNoteChanges)
    }

    @Test
    fun `save note persists and clears hasNoteChanges`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(DetailEvent.UpdateNote("saved note"))
        viewModel.onEvent(DetailEvent.SaveNote)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.hasNoteChanges)
    }

    @Test
    fun `toggle bookmark updates fact`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(DetailEvent.ToggleBookmark)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.fact?.isBookmarked == true)
    }

    @Test
    fun `share fact emits ShareText side effect`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.sideEffects.test {
            viewModel.onEvent(DetailEvent.ShareFact)
            val effect = awaitItem()
            assertTrue(effect is DetailSideEffect.ShareText)
        }
    }

    @Test
    fun `draft note survives process death via SavedStateHandle`() = runTest {
        val handle = SavedStateHandle(
            mapOf(
                DetailViewModel.FACT_ID_KEY to "fact-1",
                DetailViewModel.DRAFT_NOTE_KEY to "my draft"
            )
        )
        val viewModel = DetailViewModel(
            savedStateHandle = handle,
            factId = "fact-1",
            repository = repository,
            toggleBookmarkUseCase = ToggleBookmarkUseCase(repository),
            saveNoteUseCase = SaveNoteUseCase(repository),
            eventTracker = NoOpEventTracker()
        )
        advanceUntilIdle()

        assertEquals("my draft", viewModel.state.value.editedNote)
        assertTrue(viewModel.state.value.hasNoteChanges)
    }

    @Test
    fun `navigateBack emits NavigateBack side effect`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.sideEffects.test {
            viewModel.onEvent(DetailEvent.NavigateBack)
            val effect = awaitItem()
            assertTrue(effect is DetailSideEffect.NavigateBack)
        }
    }

    @Test
    fun `loadFact with non-existent id sets error`() = runTest {
        val viewModel = createViewModel(factId = "nonexistent")
        advanceUntilIdle()

        assertNull(viewModel.state.value.fact)
        val error = viewModel.state.value.error
        assertTrue(error is UiText.Resource)
        assertEquals(R.string.error_fact_not_found, (error as UiText.Resource).resId)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `toggleBookmark error emits ShowSnackbar`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        repository.failBookmark = true

        viewModel.sideEffects.test {
            viewModel.onEvent(DetailEvent.ToggleBookmark)
            advanceUntilIdle()
            val effect = awaitItem()
            assertTrue(effect is DetailSideEffect.ShowSnackbar)
            val message = (effect as DetailSideEffect.ShowSnackbar).message
            assertTrue(message is UiText.Resource)
        }
    }

    @Test
    fun `saveNote error emits ShowSnackbar`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        repository.failSaveNote = true

        viewModel.onEvent(DetailEvent.UpdateNote("note"))

        viewModel.sideEffects.test {
            viewModel.onEvent(DetailEvent.SaveNote)
            advanceUntilIdle()
            val effect = awaitItem()
            assertTrue(effect is DetailSideEffect.ShowSnackbar)
            val message = (effect as DetailSideEffect.ShowSnackbar).message
            assertTrue(message is UiText.Resource)
            assertEquals(R.string.error_note_save_failed, (message as UiText.Resource).resId)
        }
    }

    @Test
    fun `edits made while saving remain an unsaved recoverable draft`() = runTest {
        val saveGate = CompletableDeferred<Unit>()
        val slowRepository = object : CatFactRepository by repository {
            override suspend fun updateNote(factId: String, note: String) {
                saveGate.await()
                repository.updateNote(factId, note)
            }
        }
        val viewModel = createViewModel(repository = slowRepository)
        advanceUntilIdle()
        viewModel.onEvent(DetailEvent.UpdateNote("Submitted version"))
        viewModel.onEvent(DetailEvent.SaveNote)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.isSavingNote)

        viewModel.onEvent(DetailEvent.UpdateNote("New draft while saving"))
        saveGate.complete(Unit)
        advanceUntilIdle()

        assertEquals("Submitted version", viewModel.state.value.fact?.personalNote)
        assertEquals("New draft while saving", viewModel.state.value.editedNote)
        assertEquals("New draft while saving", savedStateHandle.get<String>(DetailViewModel.DRAFT_NOTE_KEY))
        assertTrue(viewModel.state.value.hasNoteChanges)
        assertFalse(viewModel.state.value.isSavingNote)
    }

    @Test
    fun `repeated save events persist only once while save is pending`() = runTest {
        val saveGate = CompletableDeferred<Unit>()
        var saves = 0
        val slowRepository = object : CatFactRepository by repository {
            override suspend fun updateNote(factId: String, note: String) {
                saves++
                saveGate.await()
                repository.updateNote(factId, note)
            }
        }
        val viewModel = createViewModel(repository = slowRepository)
        advanceUntilIdle()
        viewModel.onEvent(DetailEvent.UpdateNote("One save"))
        viewModel.onEvent(DetailEvent.SaveNote)
        viewModel.onEvent(DetailEvent.SaveNote)
        advanceUntilIdle()
        assertEquals(1, saves)
        saveGate.complete(Unit)
        advanceUntilIdle()
        assertFalse(viewModel.state.value.hasNoteChanges)
        assertNull(savedStateHandle.get<String>(DetailViewModel.DRAFT_NOTE_KEY))
    }
}
