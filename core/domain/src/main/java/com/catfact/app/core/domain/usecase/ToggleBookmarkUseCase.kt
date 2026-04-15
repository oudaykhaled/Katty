package com.catfact.app.core.domain.usecase

import com.catfact.app.core.domain.repository.CatFactRepository
import javax.inject.Inject

class ToggleBookmarkUseCase @Inject constructor(
    private val repository: CatFactRepository
) {
    suspend operator fun invoke(factId: String, isCurrentlyBookmarked: Boolean) {
        repository.toggleBookmark(factId, isCurrentlyBookmarked)
    }
}
