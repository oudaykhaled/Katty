package com.catfact.app.core.testing

import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.core.domain.model.PageResult
import com.catfact.app.core.domain.model.SyncStatus
import com.catfact.app.core.domain.repository.CatFactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.io.IOException

class FakeCatFactRepository : CatFactRepository {

    private val factsFlow = MutableStateFlow<List<CatFact>>(emptyList())
    private val cacheFlow = MutableStateFlow<Map<String, CatFact>>(emptyMap())
    private var activeFactIds: List<String> = emptyList()

    var failBookmark = false
    var failSaveNote = false
    var failFetchRandom = false
    var failRefresh = false
    var fakeLastPage: Int = 1
    var remotePages: Map<Int, List<CatFact>> = emptyMap()

    fun setFacts(facts: List<CatFact>) {
        activeFactIds = facts.map { it.id }.distinct()
        cacheFlow.value = facts.associateBy { it.id }
        publishFeed()
    }

    override fun observeFacts(): Flow<List<CatFact>> = factsFlow

    override fun observeFavorites(): Flow<List<CatFact>> =
        cacheFlow.map { cache -> cache.values.filter { it.isBookmarked } }

    override suspend fun getFactById(id: String): CatFact? =
        cacheFlow.value[id]

    override suspend fun fetchRandomFact(): CatFact {
        if (failFetchRandom) throw IOException("Fake fetch random failure")
        val fact = cacheFlow.value[RANDOM_FACT_ID] ?: TestFixtures.createCatFact(id = RANDOM_FACT_ID)
        cacheFlow.value = cacheFlow.value + (fact.id to fact)
        return fact
    }

    override suspend fun refreshFacts(page: Int, limit: Int): PageResult {
        require(page > 0)
        require(limit > 0)
        if (failRefresh) throw IOException("Fake refresh failure")
        val remoteFacts = remotePages[page] ?: factsFlow.value
        mergeRemoteFacts(remoteFacts)
        activeFactIds = if (page == 1) {
            remoteFacts.map { it.id }.distinct()
        } else {
            (activeFactIds + remoteFacts.map { it.id }).distinct()
        }
        publishFeed()
        return PageResult(
            facts = remoteFacts.map { cacheFlow.value.getValue(it.id) },
            currentPage = page,
            lastPage = fakeLastPage
        )
    }

    override suspend fun refreshCachedFacts(limit: Int): List<CatFact> {
        require(limit > 0)
        if (failRefresh) throw IOException("Fake refresh failure")
        val remoteFacts = remotePages[1] ?: factsFlow.value
        mergeRemoteFacts(remoteFacts)
        publishFeed()
        return remoteFacts.map { cacheFlow.value.getValue(it.id) }
    }

    override suspend fun toggleBookmark(factId: String, isCurrentlyBookmarked: Boolean) {
        if (failBookmark) throw IOException("Fake bookmark failure")
        val fact = checkNotNull(cacheFlow.value[factId]) { "Fact not found: $factId" }
        cacheFlow.value = cacheFlow.value + (factId to fact.withBookmark(!isCurrentlyBookmarked))
        publishFeed()
    }

    override suspend fun updateNote(factId: String, note: String) {
        if (failSaveNote) throw IOException("Fake save note failure")
        val fact = checkNotNull(cacheFlow.value[factId]) { "Fact not found: $factId" }
        cacheFlow.value = cacheFlow.value + (factId to fact.withNote(note))
        publishFeed()
    }

    override suspend fun searchFacts(query: String): Flow<List<CatFact>> =
        factsFlow.map { list ->
            list.filter { it.fact.contains(query, ignoreCase = true) }
        }

    private fun publishFeed() {
        factsFlow.value = activeFactIds.mapNotNull { cacheFlow.value[it] }
    }

    private fun mergeRemoteFacts(remoteFacts: List<CatFact>) {
        val mergedCache = cacheFlow.value.toMutableMap()
        remoteFacts.forEach { incoming ->
            val existing = mergedCache[incoming.id]
            mergedCache[incoming.id] = when {
                existing == null -> incoming
                existing.syncStatus != SyncStatus.SYNCED -> existing
                else -> existing.copy(fact = incoming.fact, length = incoming.length, category = incoming.category)
            }
        }
        cacheFlow.value = mergedCache
    }

    private companion object {
        const val RANDOM_FACT_ID = "random-fixture-fact"
    }
}
