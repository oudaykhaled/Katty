package com.catfact.app.core.logging

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoOpLogger @Inject constructor() : Logger {
    override fun d(tag: String, message: String) {}
    override fun i(tag: String, message: String) {}
    override fun w(tag: String, message: String, throwable: Throwable?) {}
    override fun e(tag: String, message: String, throwable: Throwable?) {}
    override fun logFactAction(factId: String, action: String, details: Map<String, Any?>) {}
    override fun logSyncEvent(event: String, details: Map<String, Any?>) {}
    override fun logNetworkRequest(method: String, url: String, durationMs: Long, statusCode: Int?) {}
    override fun logError(tag: String, error: Throwable, context: Map<String, Any?>) {}
}
