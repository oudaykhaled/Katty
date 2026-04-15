package com.catfact.app.core.network.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkModelTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `CatFactResponse deserializes correctly`() {
        val jsonStr = """{"fact":"Cats have 9 lives","length":17}"""
        val response = json.decodeFromString<CatFactResponse>(jsonStr)
        assertEquals("Cats have 9 lives", response.fact)
        assertEquals(17, response.length)
    }

    @Test
    fun `CatFactResponse serializes correctly`() {
        val response = CatFactResponse(fact = "Test fact", length = 9)
        val jsonStr = json.encodeToString(CatFactResponse.serializer(), response)
        assertEquals("""{"fact":"Test fact","length":9}""", jsonStr)
    }

    @Test
    fun `CatFactListResponse deserializes correctly`() {
        val jsonStr = """{"current_page":1,"data":[{"fact":"A fact","length":6}],"last_page":5,"total":50}"""
        val response = json.decodeFromString<CatFactListResponse>(jsonStr)
        assertEquals(1, response.currentPage)
        assertEquals(1, response.data.size)
        assertEquals("A fact", response.data[0].fact)
        assertEquals(5, response.lastPage)
        assertEquals(50, response.total)
    }

    @Test
    fun `CatFactListResponse serializes correctly`() {
        val response = CatFactListResponse(
            currentPage = 2,
            data = listOf(CatFactResponse(fact = "Fact", length = 4)),
            lastPage = 3,
            total = 25
        )
        val jsonStr = json.encodeToString(CatFactListResponse.serializer(), response)
        val decoded = json.decodeFromString<CatFactListResponse>(jsonStr)
        assertEquals(response.currentPage, decoded.currentPage)
        assertEquals(response.data.size, decoded.data.size)
        assertEquals(response.lastPage, decoded.lastPage)
        assertEquals(response.total, decoded.total)
    }

    @Test
    fun `CatFactResponse data class properties`() {
        val r1 = CatFactResponse(fact = "A", length = 1)
        val r2 = CatFactResponse(fact = "A", length = 1)
        assertEquals(r1, r2)
        assertEquals(r1.hashCode(), r2.hashCode())
        val r3 = r1.copy(fact = "B")
        assertEquals("B", r3.fact)
    }

    @Test
    fun `CatFactListResponse data class properties`() {
        val list = CatFactListResponse(currentPage = 1, data = emptyList(), lastPage = 1, total = 0)
        val copy = list.copy(total = 10)
        assertEquals(10, copy.total)
        assertEquals(list.currentPage, copy.currentPage)
    }
}
