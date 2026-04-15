package com.catfact.app.core.domain.usecase

import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.core.domain.repository.CatFactRepository
import javax.inject.Inject

class FetchRandomFactUseCase @Inject constructor(
    private val repository: CatFactRepository
) {
    suspend operator fun invoke(): CatFact = repository.fetchRandomFact()
}
