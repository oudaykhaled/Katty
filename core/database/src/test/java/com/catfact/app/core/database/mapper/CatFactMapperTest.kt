package com.catfact.app.core.database.mapper

import com.catfact.app.core.database.entity.CatFactEntity
import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.core.domain.model.FactCategory
import com.catfact.app.core.domain.model.SyncStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CatFactMapperTest {

    @Test
    fun `entity toDomain maps all fields correctly`() {
        val entity = CatFactEntity(
            id = "test-1",
            fact = "Cats have 5 toes on front paws",
            length = 30,
            isBookmarked = true,
            personalNote = "cool fact",
            syncStatus = "PENDING",
            cachedAt = 1000L
        )

        val domain = entity.toDomain()

        assertEquals("test-1", domain.id)
        assertEquals("Cats have 5 toes on front paws", domain.fact)
        assertEquals(30, domain.length)
        assertTrue(domain.isBookmarked)
        assertEquals("cool fact", domain.personalNote)
        assertEquals(SyncStatus.PENDING, domain.syncStatus)
        assertEquals(FactCategory.Short, domain.category)
    }

    @Test
    fun `domain toEntity maps all fields correctly`() {
        val domain = CatFact(
            id = "test-2",
            fact = "A group of cats is called a clowder",
            length = 36,
            isBookmarked = false,
            personalNote = "",
            syncStatus = SyncStatus.SYNCED
        )

        val entity = domain.toEntity()

        assertEquals("test-2", entity.id)
        assertEquals("A group of cats is called a clowder", entity.fact)
        assertEquals(36, entity.length)
        assertFalse(entity.isBookmarked)
        assertEquals("", entity.personalNote)
        assertEquals("SYNCED", entity.syncStatus)
    }

    @Test
    fun `entity with invalid syncStatus defaults to SYNCED`() {
        val entity = CatFactEntity(
            id = "bad-sync",
            fact = "Cats are great",
            length = 14,
            syncStatus = "INVALID_STATUS"
        )

        val domain = entity.toDomain()

        assertEquals(SyncStatus.SYNCED, domain.syncStatus)
    }

    @Test
    fun `round trip preserves data`() {
        val original = CatFact(
            id = "round-trip",
            fact = "Cats can rotate their ears 180 degrees",
            length = 38,
            isBookmarked = true,
            personalNote = "amazing",
            syncStatus = SyncStatus.FAILED
        )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original.id, roundTripped.id)
        assertEquals(original.fact, roundTripped.fact)
        assertEquals(original.length, roundTripped.length)
        assertEquals(original.isBookmarked, roundTripped.isBookmarked)
        assertEquals(original.personalNote, roundTripped.personalNote)
        assertEquals(original.syncStatus, roundTripped.syncStatus)
    }
}
