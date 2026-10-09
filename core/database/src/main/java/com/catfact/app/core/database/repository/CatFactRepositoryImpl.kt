package com.catfact.app.core.database.repository

import com.catfact.app.core.database.dao.CatFactDao
import com.catfact.app.core.database.mapper.toDomain
import com.catfact.app.core.database.mapper.toEntity
import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.core.domain.model.PageResult
import com.catfact.app.core.domain.model.ServerException
import com.catfact.app.core.domain.repository.CatFactRepository
import com.catfact.app.core.logging.Logger
import com.catfact.app.core.network.api.CatFactApi
import com.catfact.app.core.network.model.CatFactListResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatFactRepositoryImpl @Inject constructor(
    private val catFactDao: CatFactDao,
    private val catFactApi: CatFactApi,
    private val logger: Logger
) : CatFactRepository {

    override fun observeFacts(): Flow<List<CatFact>> =
        catFactDao.observeAllFacts().map { entities -> entities.map { it.toDomain() } }

    override fun observeFavorites(): Flow<List<CatFact>> =
        catFactDao.observeFavorites().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getFactById(id: String): CatFact? =
        catFactDao.getFactById(id)?.toDomain()

    override suspend fun fetchRandomFact(): CatFact {
        val start = System.currentTimeMillis()
        val response = wrapHttpException { catFactApi.getRandomFact() }
        logger.logNetworkRequest("GET", "fact", System.currentTimeMillis() - start, HTTP_OK)
        return catFactDao.insertFact(response.toEntity()).toDomain()
    }

    override suspend fun refreshFacts(page: Int, limit: Int): PageResult {
        val response = fetchPage(page, limit)
        val offset = (page.toLong() - 1L) * limit
        val entities = response.data.mapIndexed { index, fact ->
            fact.toEntity().copy(feedPosition = offset + index)
        }
        val cachedEntities = if (page == 1) {
            catFactDao.replaceAllFacts(entities)
        } else {
            catFactDao.insertFacts(entities)
        }
        return PageResult(
            facts = cachedEntities.map { it.toDomain() },
            currentPage = response.currentPage,
            lastPage = response.lastPage
        )
    }

    override suspend fun refreshCachedFacts(limit: Int): List<CatFact> {
        val response = fetchPage(page = 1, limit = limit)
        // Null positions merge cached fields while leaving existing feed membership untouched.
        return catFactDao.insertFacts(response.data.map { it.toEntity() }).map { it.toDomain() }
    }

    private suspend fun fetchPage(page: Int, limit: Int): CatFactListResponse {
        require(page > 0) { "Page must be positive" }
        require(limit > 0) { "Page size must be positive" }
        val start = System.currentTimeMillis()
        val response = wrapHttpException { catFactApi.getFacts(page = page, limit = limit) }
        logger.logNetworkRequest("GET", "facts?page=$page&limit=$limit", System.currentTimeMillis() - start, HTTP_OK)
        return response
    }

    private suspend fun <T> wrapHttpException(block: suspend () -> T): T =
        try {
            block()
        } catch (e: HttpException) {
            logger.logError("Repository", e, mapOf("httpCode" to e.code()))
            throw ServerException(e.code(), e.message(), e)
        }

    override suspend fun toggleBookmark(factId: String, isCurrentlyBookmarked: Boolean) {
        val updatedRows = catFactDao.updateBookmark(
            factId = factId,
            isBookmarked = !isCurrentlyBookmarked
        )
        check(updatedRows == 1) { "Fact is no longer available" }
    }

    override suspend fun updateNote(factId: String, note: String) {
        val updatedRows = catFactDao.updateNote(
            factId = factId,
            note = note
        )
        check(updatedRows == 1) { "Fact is no longer available" }
    }

    override suspend fun searchFacts(query: String): Flow<List<CatFact>> =
        catFactDao.searchFacts(query).map { entities -> entities.map { it.toDomain() } }

    private companion object {
        const val HTTP_OK = 200
    }
}
