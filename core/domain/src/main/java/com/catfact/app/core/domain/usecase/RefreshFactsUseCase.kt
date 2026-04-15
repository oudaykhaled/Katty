package com.catfact.app.core.domain.usecase

import com.catfact.app.core.domain.model.PageResult
import com.catfact.app.core.domain.repository.CatFactRepository
import javax.inject.Inject

class RefreshFactsUseCase @Inject constructor(
    private val repository: CatFactRepository
) {
    suspend operator fun invoke(page: Int, limit: Int): PageResult =
        repository.refreshFacts(page, limit)
}
