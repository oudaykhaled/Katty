package com.catfact.app.core.domain.usecase

import com.catfact.app.core.domain.repository.CatFactRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ToggleBookmarkUseCaseTest {

    private val repository = mockk<CatFactRepository>()
    private val useCase = ToggleBookmarkUseCase(repository)

    @Test
    fun `invoke calls repository toggleBookmark with correct params`() = runTest {
        coEvery { repository.toggleBookmark(any(), any()) } returns Unit

        useCase("fact-1", isCurrentlyBookmarked = false)

        coVerify { repository.toggleBookmark("fact-1", false) }
    }
}
