package com.catfact.app.core.telemetry

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoOpEventTracker @Inject constructor() : EventTracker {
    override fun trackFactViewed(factId: String, category: String) {}
    override fun trackBookmarkToggled(factId: String, isBookmarked: Boolean) {}
    override fun trackSyncCompleted(factCount: Int, durationMs: Long) {}
    override fun trackError(errorType: String, message: String, context: Map<String, Any?>) {}
    override fun trackScreenView(screenName: String) {}
    override fun trackUserAction(action: String, properties: Map<String, Any?>) {}
}

@Singleton
class NoOpPerformanceTracer @Inject constructor() : PerformanceTracer {
    override fun beginTrace(name: String): TraceHandle = NoOpTraceHandle
    override fun measureDuration(name: String, block: () -> Unit) = block()
    override suspend fun <T> measureSuspendDuration(name: String, block: suspend () -> T): T = block()
}

private object NoOpTraceHandle : TraceHandle {
    override fun putAttribute(key: String, value: String) {}
    override fun putMetric(key: String, value: Long) {}
    override fun stop() {}
}

@Singleton
class NoOpCrashReporter @Inject constructor() : CrashReporter {
    override fun recordException(throwable: Throwable, context: Map<String, Any?>) {}
    override fun setUserId(userId: String?) {}
    override fun setCustomKey(key: String, value: String) {}
    override fun log(message: String) {}
}
