package com.catfact.app.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.catfact.app.core.database.CatFactDatabase
import com.catfact.app.core.database.entity.CatFactEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatFactDaoTest {

    private lateinit var database: CatFactDatabase
    private lateinit var dao: CatFactDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CatFactDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.catFactDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertFact_and_getFactById_returns_it() = runTest {
        val entity = CatFactEntity(id = "1", fact = "Cats sleep 16 hours", length = 20)
        dao.insertFact(entity)

        val result = dao.getFactById("1")
        assertNotNull(result)
        assertEquals("Cats sleep 16 hours", result!!.fact)
    }

    @Test
    fun getFactById_returns_null_for_missing_id() = runTest {
        assertNull(dao.getFactById("nonexistent"))
    }

    @Test
    fun insertFacts_batch_and_observeAll_emits_ordered_by_cachedAt() = runTest {
        val older = CatFactEntity(id = "a", fact = "Older", length = 5, cachedAt = 1000L)
        val newer = CatFactEntity(id = "b", fact = "Newer", length = 5, cachedAt = 2000L)
        dao.insertFacts(listOf(older, newer))

        dao.observeAllFacts().test {
            val facts = awaitItem()
            assertEquals(2, facts.size)
            assertEquals("b", facts[0].id)
            assertEquals("a", facts[1].id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeFavorites_returns_only_bookmarked() = runTest {
        dao.insertFacts(
            listOf(
                CatFactEntity(id = "1", fact = "Bookmarked", length = 10, isBookmarked = true),
                CatFactEntity(id = "2", fact = "Not bookmarked", length = 14, isBookmarked = false)
            )
        )

        dao.observeFavorites().test {
            val favs = awaitItem()
            assertEquals(1, favs.size)
            assertEquals("1", favs[0].id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateBookmark_toggles_flag_and_sets_syncStatus() = runTest {
        dao.insertFact(CatFactEntity(id = "1", fact = "Fact", length = 4))
        dao.updateBookmark(factId = "1", isBookmarked = true, syncStatus = "PENDING")

        val updated = dao.getFactById("1")!!
        assertTrue(updated.isBookmarked)
        assertEquals("PENDING", updated.syncStatus)
    }

    @Test
    fun updateNote_persists_note_and_syncStatus() = runTest {
        dao.insertFact(CatFactEntity(id = "1", fact = "Fact", length = 4))
        dao.updateNote(factId = "1", note = "My note", syncStatus = "PENDING")

        val updated = dao.getFactById("1")!!
        assertEquals("My note", updated.personalNote)
        assertEquals("PENDING", updated.syncStatus)
    }

    @Test
    fun replaceAllFacts_preserves_pending_items() = runTest {
        dao.insertFact(CatFactEntity(id = "pending-1", fact = "Pending", length = 7, syncStatus = "PENDING"))
        dao.insertFact(CatFactEntity(id = "synced-1", fact = "Synced", length = 6, syncStatus = "SYNCED"))

        val newFacts = listOf(
            CatFactEntity(id = "pending-1", fact = "Overwrite attempt", length = 17),
            CatFactEntity(id = "new-1", fact = "Brand new", length = 9)
        )
        dao.replaceAllFacts(newFacts)

        val pending = dao.getFactById("pending-1")!!
        assertEquals("Pending", pending.fact)
        assertEquals("PENDING", pending.syncStatus)

        assertNull(dao.getFactById("synced-1"))

        val newFact = dao.getFactById("new-1")!!
        assertEquals("Brand new", newFact.fact)
    }

    @Test
    fun replaceAllFacts_also_preserves_failed_items() = runTest {
        dao.insertFact(CatFactEntity(id = "failed-1", fact = "Failed sync", length = 11, syncStatus = "FAILED"))

        dao.replaceAllFacts(
            listOf(CatFactEntity(id = "failed-1", fact = "Replacement", length = 11))
        )

        val preserved = dao.getFactById("failed-1")!!
        assertEquals("Failed sync", preserved.fact)
    }

    @Test
    fun searchFacts_returns_matching_facts() = runTest {
        dao.insertFacts(
            listOf(
                CatFactEntity(id = "1", fact = "Cats can jump 6 times their length", length = 34),
                CatFactEntity(id = "2", fact = "Dogs are loyal", length = 14),
                CatFactEntity(id = "3", fact = "A cat has 230 bones", length = 19)
            )
        )

        dao.searchFacts("cat").test {
            val results = awaitItem()
            assertEquals(2, results.size)
            assertTrue(results.all { it.fact.contains("cat", ignoreCase = true) })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun insertFact_with_same_id_replaces_existing() = runTest {
        dao.insertFact(CatFactEntity(id = "1", fact = "Original", length = 8))
        dao.insertFact(CatFactEntity(id = "1", fact = "Updated", length = 7))

        val result = dao.getFactById("1")!!
        assertEquals("Updated", result.fact)
    }

    @Test
    fun getPendingFactIds_returns_pending_and_failed() = runTest {
        dao.insertFacts(
            listOf(
                CatFactEntity(id = "p1", fact = "p", length = 1, syncStatus = "PENDING"),
                CatFactEntity(id = "f1", fact = "f", length = 1, syncStatus = "FAILED"),
                CatFactEntity(id = "s1", fact = "s", length = 1, syncStatus = "SYNCED")
            )
        )

        val pending = dao.getPendingFactIds()
        assertEquals(2, pending.size)
        assertTrue(pending.containsAll(listOf("p1", "f1")))
        assertFalse(pending.contains("s1"))
    }
}
