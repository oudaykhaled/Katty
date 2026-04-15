package com.catfact.app.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class FactCategoryTest {

    @Test
    fun `short facts are 50 chars or less`() {
        assertEquals(FactCategory.Short, FactCategory.fromLength(10))
        assertEquals(FactCategory.Short, FactCategory.fromLength(50))
    }

    @Test
    fun `medium facts are 51 to 120 chars`() {
        assertEquals(FactCategory.Medium, FactCategory.fromLength(51))
        assertEquals(FactCategory.Medium, FactCategory.fromLength(120))
    }

    @Test
    fun `long facts are over 120 chars`() {
        assertEquals(FactCategory.Long, FactCategory.fromLength(121))
        assertEquals(FactCategory.Long, FactCategory.fromLength(500))
    }

    @Test
    fun `zero length is short`() {
        assertEquals(FactCategory.Short, FactCategory.fromLength(0))
    }
}
