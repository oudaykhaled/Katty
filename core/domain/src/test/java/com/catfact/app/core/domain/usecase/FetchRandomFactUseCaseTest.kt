package com.catfact.app.core.domain.usecase

import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.core.domain.repository.CatFactRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FetchRandomFactUseCaseTest {

    private val repository = mockk<CatFactRepository>()
    private val useCase = FetchRandomFactUseCase(repository)

    @Test
    fun `invoke returns random fact from repository`() = runTest {
        val expected = CatFact(id = "1", fact = "Cats are great", length = 14)
        coEvery { repository.fetchRandomFact() } returns expected

        val result = useCase()

        assertEquals(expected, result)
    }
}
