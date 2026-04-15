package com.catfact.app.core.database.repository

import com.catfact.app.core.database.dao.CatFactDao
import com.catfact.app.core.database.mapper.toDomain
import com.catfact.app.core.database.mapper.toEntity
import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.core.domain.model.PageResult
import com.catfact.app.core.domain.model.ServerException
import com.catfact.app.core.domain.model.SyncStatus
import com.catfact.app.core.domain.repository.CatFactRepository
import com.catfact.app.core.logging.Logger
import com.catfact.app.core.network.api.CatFactApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.util.UUID
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
        logger.logNetworkRequest("GET", "fact", System.currentTimeMillis() - start, 200)
        val entity = response.toEntity(id = UUID.randomUUID().toString())
        catFactDao.insertFact(entity)
        return entity.toDomain()
    }

    override suspend fun refreshFacts(page: Int, limit: Int): PageResult {
        val start = System.currentTimeMillis()
        val response = wrapHttpException { catFactApi.getFacts(page = page, limit = limit) }
        logger.logNetworkRequest("GET", "facts?page=$page&limit=$limit", System.currentTimeMillis() - start, 200)
        val entities = response.data.map { it.toEntity() }
        if (page == 1) {
            catFactDao.replaceAllFacts(entities)
        } else {
            catFactDao.insertFacts(entities)
        }
        return PageResult(
            facts = entities.map { it.toDomain() },
            currentPage = response.currentPage,
            lastPage = response.lastPage
        )
    }

    private suspend fun <T> wrapHttpException(block: suspend () -> T): T =
        try {
            block()
        } catch (e: HttpException) {
            logger.logError("Repository", e, mapOf("httpCode" to e.code()))
            throw ServerException(e.code(), e.message())
        }

    override suspend fun toggleBookmark(factId: String, isCurrentlyBookmarked: Boolean) {
        catFactDao.updateBookmark(
            factId = factId,
            isBookmarked = !isCurrentlyBookmarked,
            syncStatus = SyncStatus.SYNCED.name
        )
    }

    override suspend fun updateNote(factId: String, note: String) {
        catFactDao.updateNote(
            factId = factId,
            note = note,
            syncStatus = SyncStatus.SYNCED.name
        )
    }

    override suspend fun searchFacts(query: String): Flow<List<CatFact>> =
        catFactDao.searchFacts(query).map { entities -> entities.map { it.toDomain() } }
}
