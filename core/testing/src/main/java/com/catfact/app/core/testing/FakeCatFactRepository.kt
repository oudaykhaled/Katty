package com.catfact.app.core.testing

import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.core.domain.model.PageResult
import com.catfact.app.core.domain.model.SyncStatus
import com.catfact.app.core.domain.repository.CatFactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeCatFactRepository : CatFactRepository {

    private val factsFlow = MutableStateFlow<List<CatFact>>(emptyList())

    var failBookmark = false
    var failSaveNote = false
    var failFetchRandom = false
    var failRefresh = false
    var fakeLastPage: Int = 1

    fun setFacts(facts: List<CatFact>) {
        factsFlow.value = facts
    }

    override fun observeFacts(): Flow<List<CatFact>> = factsFlow

    override fun observeFavorites(): Flow<List<CatFact>> =
        factsFlow.map { list -> list.filter { it.isBookmarked } }

    override suspend fun getFactById(id: String): CatFact? =
        factsFlow.value.find { it.id == id }

    override suspend fun fetchRandomFact(): CatFact {
        if (failFetchRandom) throw RuntimeException("Fake fetch random failure")
        val fact = TestFixtures.createCatFact(id = "random-${System.nanoTime()}")
        factsFlow.value = factsFlow.value + fact
        return fact
    }

    override suspend fun refreshFacts(page: Int, limit: Int): PageResult {
        if (failRefresh) throw RuntimeException("Fake refresh failure")
        return PageResult(
            facts = factsFlow.value,
            currentPage = page,
            lastPage = fakeLastPage
        )
    }

    override suspend fun toggleBookmark(factId: String, isCurrentlyBookmarked: Boolean) {
        if (failBookmark) throw RuntimeException("Fake bookmark failure")
        factsFlow.value = factsFlow.value.map { fact ->
            if (fact.id == factId) fact.withBookmark(!isCurrentlyBookmarked) else fact
        }
    }

    override suspend fun updateNote(factId: String, note: String) {
        if (failSaveNote) throw RuntimeException("Fake save note failure")
        factsFlow.value = factsFlow.value.map { fact ->
            if (fact.id == factId) fact.withNote(note).withSyncStatus(SyncStatus.PENDING) else fact
        }
    }

    override suspend fun searchFacts(query: String): Flow<List<CatFact>> =
        factsFlow.map { list ->
            list.filter { it.fact.contains(query, ignoreCase = true) }
        }
}
