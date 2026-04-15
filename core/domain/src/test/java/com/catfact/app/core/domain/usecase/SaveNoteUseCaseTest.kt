package com.catfact.app.core.domain.usecase

import com.catfact.app.core.domain.repository.CatFactRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SaveNoteUseCaseTest {

    private val repository = mockk<CatFactRepository>()
    private val useCase = SaveNoteUseCase(repository)

    @Test
    fun `invoke calls repository updateNote`() = runTest {
        coEvery { repository.updateNote(any(), any()) } returns Unit

        useCase("fact-1", "my note")

        coVerify { repository.updateNote("fact-1", "my note") }
    }
}
