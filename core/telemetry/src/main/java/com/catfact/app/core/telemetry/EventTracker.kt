package com.catfact.app.core.telemetry

interface EventTracker {
    fun trackFactViewed(factId: String, category: String)
    fun trackBookmarkToggled(factId: String, isBookmarked: Boolean)
    fun trackSyncCompleted(factCount: Int, durationMs: Long)
    fun trackError(errorType: String, message: String, context: Map<String, Any?> = emptyMap())
    fun trackScreenView(screenName: String)
    fun trackUserAction(action: String, properties: Map<String, Any?> = emptyMap())
}
