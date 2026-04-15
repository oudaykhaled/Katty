package com.catfact.app.core.telemetry

interface CrashReporter {
    fun recordException(throwable: Throwable, context: Map<String, Any?> = emptyMap())
    fun setUserId(userId: String?)
    fun setCustomKey(key: String, value: String)
    fun log(message: String)
}
