package com.catfact.app.core.telemetry

interface PerformanceTracer {
    fun beginTrace(name: String): TraceHandle
    fun measureDuration(name: String, block: () -> Unit)
    suspend fun <T> measureSuspendDuration(name: String, block: suspend () -> T): T
}

interface TraceHandle {
    fun putAttribute(key: String, value: String)
    fun putMetric(key: String, value: Long)
    fun stop()
}
