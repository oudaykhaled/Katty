package com.catfact.app.core.sync.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.catfact.app.core.domain.model.CatFact
import com.catfact.app.core.domain.model.PageResult
import com.catfact.app.core.domain.model.SyncStatus
import com.catfact.app.core.domain.repository.CatFactRepository
import com.catfact.app.core.logging.Logger
import com.catfact.app.core.telemetry.NoOpEventTracker
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CatFactSyncWorkerTest {

    private val context: Context = mockk(relaxed = true)
    private val workerParams: WorkerParameters = mockk(relaxed = true)

    @Before
    fun setUp() {
        every { workerParams.runAttemptCount } returns 0
    }

    @Test
    fun `work name is correct`() {
        assertEquals("cat_fact_sync", CatFactSyncWorker.WORK_NAME)
    }

    @Test
    fun `doWork refreshes cache without resetting interactive feed`() = runTest {
        val repository = FakeSyncRepository(shouldFail = false)
        val worker = CatFactSyncWorker(
            appContext = context,
            workerParams = workerParams,
            repository = repository,
            logger = NoOpTestLogger(),
            eventTracker = NoOpEventTracker()
        )
        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.success(), result)
        assertEquals(listOf(50), repository.cacheRefreshLimits)
    }

    @Test
    fun `doWork returns retry when repository fails and attempt is low`() = runTest {
        every { workerParams.runAttemptCount } returns 0
        val worker = CatFactSyncWorker(
            appContext = context,
            workerParams = workerParams,
            repository = FakeSyncRepository(shouldFail = true),
            logger = NoOpTestLogger(),
            eventTracker = NoOpEventTracker()
        )
        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.retry(), result)
    }

    @Test
    fun `doWork returns failure when max retries exceeded`() = runTest {
        every { workerParams.runAttemptCount } returns 5
        val worker = CatFactSyncWorker(
            appContext = context,
            workerParams = workerParams,
            repository = FakeSyncRepository(shouldFail = true),
            logger = NoOpTestLogger(),
            eventTracker = NoOpEventTracker()
        )
        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.failure(), result)
    }

    @Test
    fun `cancellation propagates without retrying cancelled work`() = runTest {
        val worker = CatFactSyncWorker(
            appContext = context,
            workerParams = workerParams,
            repository = FakeSyncRepository(shouldFail = false, cancelRefresh = true),
            logger = NoOpTestLogger(),
            eventTracker = NoOpEventTracker()
        )

        val failure = runCatching { worker.doWork() }.exceptionOrNull()

        assertTrue(failure is CancellationException)
    }
}

private class FakeSyncRepository(
    private val shouldFail: Boolean,
    private val cancelRefresh: Boolean = false
) : CatFactRepository {
    val cacheRefreshLimits = mutableListOf<Int>()
    override fun observeFacts(): Flow<List<CatFact>> = flowOf(emptyList())
    override fun observeFavorites(): Flow<List<CatFact>> = flowOf(emptyList())
    override suspend fun getFactById(id: String): CatFact? = null
    override suspend fun fetchRandomFact(): CatFact = CatFact(
        id = "r", fact = "f", length = 1, isBookmarked = false,
        personalNote = "", syncStatus = SyncStatus.SYNCED
    )
    override suspend fun refreshFacts(page: Int, limit: Int): PageResult =
        error("Background work must not change the interactive feed")
    override suspend fun refreshCachedFacts(limit: Int): List<CatFact> {
        cacheRefreshLimits += limit
        if (cancelRefresh) throw CancellationException("Worker cancelled")
        if (shouldFail) throw java.io.IOException("Sync failed")
        return emptyList()
    }
    override suspend fun toggleBookmark(factId: String, isCurrentlyBookmarked: Boolean) = Unit
    override suspend fun updateNote(factId: String, note: String) = Unit
    override suspend fun searchFacts(query: String): Flow<List<CatFact>> = flowOf(emptyList())
}

private class NoOpTestLogger : Logger {
    override fun d(tag: String, message: String) = Unit
    override fun i(tag: String, message: String) = Unit
    override fun w(tag: String, message: String, throwable: Throwable?) = Unit
    override fun e(tag: String, message: String, throwable: Throwable?) = Unit
    override fun logFactAction(factId: String, action: String, details: Map<String, Any?>) = Unit
    override fun logSyncEvent(event: String, details: Map<String, Any?>) = Unit
    override fun logNetworkRequest(method: String, url: String, durationMs: Long, statusCode: Int?) = Unit
    override fun logError(tag: String, error: Throwable, context: Map<String, Any?>) = Unit
}
