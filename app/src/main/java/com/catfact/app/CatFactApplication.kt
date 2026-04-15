package com.catfact.app

import com.catfact.app.core.sync.manager.CatFactSyncManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CatFactApplication : BaseApplication() {

    @Inject
    lateinit var syncManager: CatFactSyncManager

    override fun onCreate() {
        super.onCreate()
        syncManager.schedulePeriodicSync()
    }
}
