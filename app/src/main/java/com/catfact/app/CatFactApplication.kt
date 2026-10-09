package com.catfact.app

import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.catfact.app.core.sync.manager.CatFactSyncManager
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CatFactApplication : BaseApplication(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var syncManager: Lazy<CatFactSyncManager>

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Resolve after all Application fields are injected: WorkManager reads the factory above.
        syncManager.get().schedulePeriodicSync()
    }
}
