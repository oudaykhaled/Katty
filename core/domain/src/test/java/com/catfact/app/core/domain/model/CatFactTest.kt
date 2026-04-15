package com.catfact.app.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CatFactTest {

    private val fact = CatFact(
        id = "fact-1",
        fact = "Cats sleep 70% of their lives.",
        length = 31,
        isBookmarked = false,
        personalNote = "",
        syncStatus = SyncStatus.SYNCED
    )

    @Test
    fun `withBookmark returns copy with updated bookmark`() {
        val bookmarked = fact.withBookmark(true)
        assertTrue(bookmarked.isBookmarked)
        assertEquals(fact.id, bookmarked.id)
        assertEquals(fact.fact, bookmarked.fact)
    }

    @Test
    fun `withNote returns copy with updated note`() {
        val noted = fact.withNote("interesting!")
        assertEquals("interesting!", noted.personalNote)
        assertEquals(fact.id, noted.id)
    }

    @Test
    fun `withSyncStatus returns copy with updated status`() {
        val pending = fact.withSyncStatus(SyncStatus.PENDING)
        assertEquals(SyncStatus.PENDING, pending.syncStatus)
    }

    @Test
    fun `default category is computed from length`() {
        assertEquals(FactCategory.Short, fact.category)
    }

    @Test
    fun `copy preserves all fields`() {
        val modified = fact.withBookmark(true).withNote("test").withSyncStatus(SyncStatus.PENDING)
        assertTrue(modified.isBookmarked)
        assertEquals("test", modified.personalNote)
        assertEquals(SyncStatus.PENDING, modified.syncStatus)
        assertEquals(fact.id, modified.id)
        assertEquals(fact.fact, modified.fact)
        assertEquals(fact.length, modified.length)
    }
}
