package com.catfact.app

import androidx.hilt.work.HiltWorkerFactory
import io.mockk.mockk
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = BaseApplication::class)
class CatFactApplicationTest {

    @Test
    fun workManagerConfiguration_usesInjectedWorkerFactory() {
        val factory = mockk<HiltWorkerFactory>()
        val application = CatFactApplication().apply { workerFactory = factory }

        assertSame(factory, application.workManagerConfiguration.workerFactory)
    }
}
