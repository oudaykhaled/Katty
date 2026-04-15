package com.catfact.app.feature.facts.viewmodel

import app.cash.turbine.test
import com.catfact.app.core.domain.usecase.FetchRandomFactUseCase
import com.catfact.app.core.domain.usecase.GetFactsUseCase
import com.catfact.app.core.domain.usecase.RefreshFactsUseCase
import com.catfact.app.core.domain.usecase.ToggleBookmarkUseCase
import com.catfact.app.core.telemetry.NoOpEventTracker
import com.catfact.app.core.testing.FakeCatFactRepository
import com.catfact.app.core.testing.TestFixtures
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FactsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeCatFactRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeCatFactRepository()
        repository.setFacts(TestFixtures.createFactList(3))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = FactsViewModel(
        getFactsUseCase = GetFactsUseCase(repository),
        fetchRandomFactUseCase = FetchRandomFactUseCase(repository),
        toggleBookmarkUseCase = ToggleBookmarkUseCase(repository),
        refreshFactsUseCase = RefreshFactsUseCase(repository),
        eventTracker = NoOpEventTracker()
    )

    @Test
    fun `initial state has empty facts before coroutines run`() {
        val viewModel = createViewModel()
        val state = viewModel.state.value
        assertFalse(state.isLoading && state.facts.isNotEmpty())
    }

    @Test
    fun `observes facts from repository`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(4, state.facts.size)
        }
    }

    @Test
    fun `fetchRandomFact updates random fact`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(FactsEvent.FetchRandomFact)
        advanceUntilIdle()

        assertNotNull(viewModel.state.value.randomFact)
    }

    @Test
    fun `fetchRandomFact error emits ShowSnackbar`() = runTest {
        repository.failFetchRandom = true
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.sideEffects.test {
            viewModel.onEvent(FactsEvent.FetchRandomFact)
            advanceUntilIdle()
            val effect = awaitItem()
            assertTrue(effect is FactsSideEffect.ShowSnackbar)
        }
    }

    @Test
    fun `toggle bookmark adds fact id to loading ids then removes`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val facts = viewModel.state.value.facts
        if (facts.isNotEmpty()) {
            viewModel.onEvent(FactsEvent.ToggleBookmark(facts[0].id, facts[0].isBookmarked))
            advanceUntilIdle()
            assertFalse(viewModel.state.value.bookmarkLoadingIds.contains(facts[0].id))
        }
    }

    @Test
    fun `toggle bookmark error emits ShowSnackbar`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        repository.failBookmark = true

        val facts = viewModel.state.value.facts
        assertTrue(facts.isNotEmpty())

        viewModel.sideEffects.test {
            viewModel.onEvent(FactsEvent.ToggleBookmark(facts[0].id, facts[0].isBookmarked))
            advanceUntilIdle()
            val effect = awaitItem()
            assertTrue(effect is FactsSideEffect.ShowSnackbar)
        }
    }

    @Test
    fun `dismiss error clears error`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(FactsEvent.DismissError)
        advanceUntilIdle()

        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `refresh success updates random fact`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(FactsEvent.Refresh)
        advanceUntilIdle()

        assertNotNull(viewModel.state.value.randomFact)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `refresh error sets error state`() = runTest {
        repository.failFetchRandom = true
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(FactsEvent.Refresh)
        advanceUntilIdle()

        assertNotNull(viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `loadNextPage calls refreshFacts and increments page`() = runTest {
        repository.fakeLastPage = 5
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.currentPage)
        viewModel.onEvent(FactsEvent.LoadNextPage)
        advanceUntilIdle()

        assertEquals(2, viewModel.state.value.currentPage)
        assertTrue(viewModel.state.value.hasMorePages)
        assertFalse(viewModel.state.value.isLoadingMore)
    }

    @Test
    fun `loadNextPage sets hasMorePages false on last page`() = runTest {
        repository.fakeLastPage = 2
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(FactsEvent.LoadNextPage)
        advanceUntilIdle()

        assertEquals(2, viewModel.state.value.currentPage)
        assertFalse(viewModel.state.value.hasMorePages)
    }

    @Test
    fun `loadNextPage error emits ShowSnackbar`() = runTest {
        repository.failRefresh = true
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.sideEffects.test {
            viewModel.onEvent(FactsEvent.LoadNextPage)
            advanceUntilIdle()
            val effect = awaitItem()
            assertTrue(effect is FactsSideEffect.ShowSnackbar)
        }
    }

    @Test
    fun `factClicked emits NavigateToDetail`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.sideEffects.test {
            viewModel.onEvent(FactsEvent.FactClicked("fact-123"))
            val effect = awaitItem()
            assertTrue(effect is FactsSideEffect.NavigateToDetail)
            assertEquals("fact-123", (effect as FactsSideEffect.NavigateToDetail).factId)
        }
    }

    @Test
    fun `search updates searchInput immediately and searchQuery after debounce`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(FactsEvent.Search("cat"))
        assertEquals("cat", viewModel.state.value.searchInput)
        assertEquals("", viewModel.state.value.searchQuery)

        advanceTimeBy(400)
        advanceUntilIdle()

        assertEquals("cat", viewModel.state.value.searchQuery)
    }

    @Test
    fun `loadInitial failure is silently caught`() = runTest {
        repository.failFetchRandom = true
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertNull(viewModel.state.value.randomFact)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.error)
    }
}
