package com.catfact.app.core.sync.manager

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.catfact.app.core.sync.worker.CatFactSyncWorker
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CatFactSyncManagerTest {

    private lateinit var workManager: WorkManager
    private lateinit var syncManager: CatFactSyncManager

    @Before
    fun setUp() {
        workManager = mockk(relaxed = true)
        syncManager = CatFactSyncManager(workManager)
    }

    @Test
    fun `schedulePeriodicSync enqueues unique periodic work`() {
        syncManager.schedulePeriodicSync()

        val nameSlot = slot<String>()
        val policySlot = slot<ExistingPeriodicWorkPolicy>()
        val requestSlot = slot<PeriodicWorkRequest>()

        verify {
            workManager.enqueueUniquePeriodicWork(
                capture(nameSlot),
                capture(policySlot),
                capture(requestSlot)
            )
        }

        assertEquals(CatFactSyncWorker.WORK_NAME, nameSlot.captured)
        assertEquals(ExistingPeriodicWorkPolicy.KEEP, policySlot.captured)
    }

    @Test
    fun `requestImmediateSync enqueues one-time work`() {
        syncManager.requestImmediateSync()

        verify {
            workManager.enqueue(any<OneTimeWorkRequest>())
        }
    }
}
