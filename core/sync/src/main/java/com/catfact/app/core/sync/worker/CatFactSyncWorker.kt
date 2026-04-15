package com.catfact.app.core.sync.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.catfact.app.core.domain.repository.CatFactRepository
import com.catfact.app.core.logging.Logger
import com.catfact.app.core.telemetry.EventTracker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CatFactSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: CatFactRepository,
    private val logger: Logger,
    private val eventTracker: EventTracker
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        logger.logSyncEvent("sync_started", mapOf("runAttempt" to runAttemptCount))
        val startTime = System.currentTimeMillis()
        return try {
            val result = repository.refreshFacts(page = 1, limit = SYNC_PAGE_SIZE)
            val durationMs = System.currentTimeMillis() - startTime
            logger.logSyncEvent("sync_completed", mapOf("factCount" to result.facts.size))
            eventTracker.trackSyncCompleted(result.facts.size, durationMs)
            Result.success()
        } catch (e: Exception) {
            logger.logError("CatFactSyncWorker", e, mapOf("runAttempt" to runAttemptCount))
            if (runAttemptCount < MAX_RETRIES) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "cat_fact_sync"
        private const val SYNC_PAGE_SIZE = 50
        private const val MAX_RETRIES = 3
    }
}
