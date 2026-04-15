package com.catfact.app.core.domain.usecase

import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.core.domain.repository.CatFactRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFactsUseCase @Inject constructor(
    private val repository: CatFactRepository
) {
    operator fun invoke(): Flow<List<CatFact>> = repository.observeFacts()
}
