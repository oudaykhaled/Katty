package com.catfact.app.core.telemetry

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class NoOpTelemetryTest {

    @Test
    fun `NoOpPerformanceTracer beginTrace returns non-null handle`() {
        val tracer = NoOpPerformanceTracer()
        val handle = tracer.beginTrace("test-trace")
        assertNotNull(handle)
    }

    @Test
    fun `NoOpPerformanceTracer measureDuration executes block and returns`() {
        val tracer = NoOpPerformanceTracer()
        var counter = 0
        tracer.measureDuration("test") { counter++ }
        assertEquals(1, counter)
    }

    @Test
    fun `NoOpPerformanceTracer measureSuspendDuration returns block result`() = runTest {
        val tracer = NoOpPerformanceTracer()
        val result = tracer.measureSuspendDuration("test") { 42 }
        assertEquals(42, result)
    }

    @Test
    fun `NoOpPerformanceTracer measureSuspendDuration preserves suspend semantics`() = runTest {
        val tracer = NoOpPerformanceTracer()
        val items = mutableListOf<String>()
        tracer.measureSuspendDuration("test") {
            items.add("inside")
        }
        items.add("after")
        assertEquals(listOf("inside", "after"), items)
    }

    @Test
    fun `NoOpTraceHandle lifecycle can be called without errors`() {
        val tracer = NoOpPerformanceTracer()
        val handle = tracer.beginTrace("lifecycle-test")
        handle.putAttribute("key", "value")
        handle.putMetric("metric", 42L)
        handle.stop()
        assertNotNull(handle)
    }

    @Test
    fun `NoOpEventTracker interface default parameters work through concrete type`() {
        val tracker: EventTracker = NoOpEventTracker()
        tracker.trackError("type", "msg")
        tracker.trackUserAction("action")
        assertNotNull(tracker)
    }

    @Test
    fun `NoOpCrashReporter interface default parameters work through concrete type`() {
        val reporter: CrashReporter = NoOpCrashReporter()
        reporter.recordException(RuntimeException("e"))
        assertNotNull(reporter)
    }
}
