package com.catfact.app.feature.favorites.viewmodel

import app.cash.turbine.test
import com.catfact.app.core.domain.usecase.GetFavoritesUseCase
import com.catfact.app.core.domain.usecase.ToggleBookmarkUseCase
import com.catfact.app.core.telemetry.NoOpEventTracker
import com.catfact.app.core.testing.FakeCatFactRepository
import com.catfact.app.core.testing.TestFixtures
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import com.catfact.app.core.designsystem.util.UiText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeCatFactRepository
    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeCatFactRepository()
        viewModel = FavoritesViewModel(
            getFavoritesUseCase = GetFavoritesUseCase(repository),
            toggleBookmarkUseCase = ToggleBookmarkUseCase(repository),
            eventTracker = NoOpEventTracker()
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty favorites`() {
        assertTrue(viewModel.state.value.favorites.isEmpty())
    }

    @Test
    fun `observes bookmarked facts`() = runTest {
        val facts = TestFixtures.createFactList(3).map { it.withBookmark(true) }
        repository.setFacts(facts)
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(3, state.favorites.size)
        }
    }

    @Test
    fun `remove bookmark sends toggle to repository`() = runTest {
        val facts = listOf(TestFixtures.createCatFact(id = "f1", isBookmarked = true))
        repository.setFacts(facts)
        advanceUntilIdle()

        viewModel.onEvent(FavoritesEvent.RemoveBookmark("f1"))
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.favorites.isEmpty())
        }
    }

    @Test
    fun `fact clicked emits navigate side effect`() = runTest {
        advanceUntilIdle()

        viewModel.sideEffects.test {
            viewModel.onEvent(FavoritesEvent.FactClicked("f1"))
            val effect = awaitItem()
            assertTrue(effect is FavoritesSideEffect.NavigateToDetail)
            assertEquals("f1", (effect as FavoritesSideEffect.NavigateToDetail).factId)
        }
    }

    @Test
    fun `dismiss error clears error state`() = runTest {
        advanceUntilIdle()

        viewModel.onEvent(FavoritesEvent.DismissError)
        advanceUntilIdle()

        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `removeBookmark error emits ShowSnackbar`() = runTest {
        val facts = listOf(TestFixtures.createCatFact(id = "f1", isBookmarked = true))
        repository.setFacts(facts)
        advanceUntilIdle()
        repository.failBookmark = true

        viewModel.sideEffects.test {
            viewModel.onEvent(FavoritesEvent.RemoveBookmark("f1"))
            advanceUntilIdle()
            val effect = awaitItem()
            assertTrue(effect is FavoritesSideEffect.ShowSnackbar)
            val message = (effect as FavoritesSideEffect.ShowSnackbar).message
            assertTrue(message is UiText.Resource)
        }
    }
}
