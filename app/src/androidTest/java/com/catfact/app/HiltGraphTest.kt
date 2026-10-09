package com.catfact.app

import androidx.hilt.work.HiltWorkerFactory
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.catfact.app.core.domain.repository.CatFactRepository
import com.catfact.app.core.sync.worker.CatFactSyncWorker
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidTest
class HiltGraphTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: CatFactRepository

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun hiltDependencyGraphResolves() {
        assertNotNull(repository)
    }

    @Test
    fun injectedSyncWorker_executesThroughWorkManager() = runBlocking {
        // The test Application omits production scheduling; exercise the generated worker binding explicitly.
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        WorkManager.initialize(
            context,
            Configuration.Builder().setWorkerFactory(workerFactory).build()
        )
        val workManager = WorkManager.getInstance(context)
        val request = OneTimeWorkRequestBuilder<CatFactSyncWorker>().build()
        workManager.enqueue(request).result.get(ENQUEUE_TIMEOUT_SECONDS, TimeUnit.SECONDS)

        val completedWork = withTimeout(WORK_TIMEOUT_MILLIS) {
            workManager.getWorkInfoByIdFlow(request.id).filterNotNull().first { it.state.isFinished }
        }

        assertEquals(completedWork.toString(), WorkInfo.State.SUCCEEDED, completedWork.state)
    }

    private companion object {
        const val ENQUEUE_TIMEOUT_SECONDS = 10L
        const val WORK_TIMEOUT_MILLIS = 30_000L
    }
}
