package com.catfact.app.core.database.repository

import app.cash.turbine.test
import com.catfact.app.core.database.dao.CatFactDao
import com.catfact.app.core.database.entity.CatFactEntity
import com.catfact.app.core.domain.model.SyncStatus
import com.catfact.app.core.logging.Logger
import com.catfact.app.core.network.api.CatFactApi
import com.catfact.app.core.network.model.CatFactListResponse
import com.catfact.app.core.network.model.CatFactResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CatFactRepositoryImplTest {

    private lateinit var dao: CatFactDao
    private lateinit var api: CatFactApi
    private lateinit var logger: Logger
    private lateinit var repository: CatFactRepositoryImpl

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        api = mockk()
        logger = mockk(relaxed = true)
        repository = CatFactRepositoryImpl(catFactDao = dao, catFactApi = api, logger = logger)
    }

    @Test
    fun `observeFacts maps entities to domain models`() = runTest {
        val entities = listOf(
            CatFactEntity(id = "1", fact = "Cats sleep a lot", length = 17)
        )
        every { dao.observeAllFacts() } returns flowOf(entities)

        repository.observeFacts().test {
            val facts = awaitItem()
            assertEquals(1, facts.size)
            assertEquals("Cats sleep a lot", facts[0].fact)
            assertEquals(17, facts[0].length)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `observeFavorites maps bookmarked entities`() = runTest {
        val entities = listOf(
            CatFactEntity(id = "1", fact = "Fact 1", length = 6, isBookmarked = true)
        )
        every { dao.observeFavorites() } returns flowOf(entities)

        repository.observeFavorites().test {
            val favorites = awaitItem()
            assertEquals(1, favorites.size)
            assertTrue(favorites[0].isBookmarked)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getFactById returns mapped domain model when found`() = runTest {
        val entity = CatFactEntity(id = "test-id", fact = "Test fact", length = 9)
        coEvery { dao.getFactById("test-id") } returns entity

        val result = repository.getFactById("test-id")
        assertNotNull(result)
        assertEquals("Test fact", result!!.fact)
    }

    @Test
    fun `getFactById returns null when not found`() = runTest {
        coEvery { dao.getFactById("nonexistent") } returns null

        val result = repository.getFactById("nonexistent")
        assertNull(result)
    }

    @Test
    fun `fetchRandomFact calls api and inserts into dao`() = runTest {
        coEvery { api.getRandomFact() } returns CatFactResponse(fact = "Random cat fact", length = 15)

        val fact = repository.fetchRandomFact()

        assertEquals("Random cat fact", fact.fact)
        assertEquals(15, fact.length)
        coVerify { dao.insertFact(any()) }
    }

    @Test
    fun `refreshFacts page 1 calls replaceAllFacts`() = runTest {
        coEvery { api.getFacts(page = 1, limit = 10) } returns CatFactListResponse(
            currentPage = 1,
            data = listOf(CatFactResponse(fact = "New fact", length = 8)),
            lastPage = 1,
            total = 1
        )

        val result = repository.refreshFacts(page = 1, limit = 10)

        assertEquals(1, result.facts.size)
        assertEquals("New fact", result.facts[0].fact)
        assertEquals(1, result.currentPage)
        assertEquals(1, result.lastPage)
        assertFalse(result.hasMorePages)
        coVerify { dao.replaceAllFacts(any()) }
    }

    @Test
    fun `refreshFacts page 2 calls insertFacts`() = runTest {
        coEvery { api.getFacts(page = 2, limit = 10) } returns CatFactListResponse(
            currentPage = 2,
            data = listOf(CatFactResponse(fact = "Page two fact", length = 13)),
            lastPage = 2,
            total = 2
        )

        val result = repository.refreshFacts(page = 2, limit = 10)

        assertEquals(2, result.currentPage)
        assertEquals(2, result.lastPage)
        assertFalse(result.hasMorePages)
        coVerify { dao.insertFacts(any()) }
    }

    @Test
    fun `refreshFacts reports hasMorePages when not on last page`() = runTest {
        coEvery { api.getFacts(page = 1, limit = 10) } returns CatFactListResponse(
            currentPage = 1,
            data = listOf(CatFactResponse(fact = "First page fact", length = 15)),
            lastPage = 5,
            total = 50
        )

        val result = repository.refreshFacts(page = 1, limit = 10)

        assertTrue(result.hasMorePages)
        assertEquals(1, result.currentPage)
        assertEquals(5, result.lastPage)
    }

    @Test
    fun `toggleBookmark calls dao with inverted bookmark`() = runTest {
        repository.toggleBookmark("b1", isCurrentlyBookmarked = false)

        coVerify {
            dao.updateBookmark(
                factId = "b1",
                isBookmarked = true,
                syncStatus = SyncStatus.SYNCED.name
            )
        }
    }

    @Test
    fun `toggleBookmark from bookmarked to unbookmarked`() = runTest {
        repository.toggleBookmark("b1", isCurrentlyBookmarked = true)

        coVerify {
            dao.updateBookmark(
                factId = "b1",
                isBookmarked = false,
                syncStatus = SyncStatus.SYNCED.name
            )
        }
    }

    @Test
    fun `updateNote calls dao with note and synced status`() = runTest {
        repository.updateNote("n1", "My note")

        coVerify {
            dao.updateNote(
                factId = "n1",
                note = "My note",
                syncStatus = SyncStatus.SYNCED.name
            )
        }
    }

    @Test
    fun `searchFacts delegates to dao and maps results`() = runTest {
        val entities = listOf(
            CatFactEntity(id = "s1", fact = "Cats love fish", length = 14)
        )
        every { dao.searchFacts("Cats") } returns flowOf(entities)

        repository.searchFacts("Cats").test {
            val results = awaitItem()
            assertEquals(1, results.size)
            assertEquals("s1", results[0].id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fetchRandomFact generates UUID for id`() = runTest {
        coEvery { api.getRandomFact() } returns CatFactResponse(fact = "A fact", length = 6)
        val entitySlot = slot<CatFactEntity>()
        coEvery { dao.insertFact(capture(entitySlot)) } returns Unit

        repository.fetchRandomFact()

        assertTrue(entitySlot.captured.id.isNotEmpty())
    }

    @Test
    fun `refreshFacts page 1 generates deterministic UUID from fact content`() = runTest {
        coEvery { api.getFacts(page = 1, limit = 10) } returns CatFactListResponse(
            currentPage = 1,
            data = listOf(CatFactResponse(fact = "Same fact", length = 9)),
            lastPage = 1,
            total = 1
        )
        val entitiesSlot = slot<List<CatFactEntity>>()
        coEvery { dao.replaceAllFacts(capture(entitiesSlot)) } returns Unit

        repository.refreshFacts(page = 1, limit = 10)

        val id1 = entitiesSlot.captured[0].id

        coEvery { dao.replaceAllFacts(capture(entitiesSlot)) } returns Unit
        repository.refreshFacts(page = 1, limit = 10)

        assertEquals(id1, entitiesSlot.captured[0].id)
    }
}
