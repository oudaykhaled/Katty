package com.catfact.app.core.domain.repository

import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.core.domain.model.PageResult
import kotlinx.coroutines.flow.Flow

interface CatFactRepository {
    fun observeFacts(): Flow<List<CatFact>>
    fun observeFavorites(): Flow<List<CatFact>>
    suspend fun getFactById(id: String): CatFact?
    suspend fun fetchRandomFact(): CatFact
    suspend fun refreshFacts(page: Int, limit: Int): PageResult
    suspend fun toggleBookmark(factId: String, isCurrentlyBookmarked: Boolean)
    suspend fun updateNote(factId: String, note: String)
    suspend fun searchFacts(query: String): Flow<List<CatFact>>
}
