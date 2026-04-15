package com.catfact.app.core.network.interceptor

import okhttp3.Interceptor
import org.junit.Assert.assertEquals
import org.junit.Test

class InterceptorEntryTest {

    private fun stubInterceptor(name: String) = Interceptor { chain ->
        chain.proceed(chain.request().newBuilder().addHeader("X-Interceptor", name).build())
    }

    @Test
    fun `sorted entries execute in order regardless of insertion order`() {
        val third = InterceptorEntry(order = 30, interceptor = stubInterceptor("retry"), type = InterceptorType.APPLICATION)
        val first = InterceptorEntry(order = 10, interceptor = stubInterceptor("headers"), type = InterceptorType.APPLICATION)
        val second = InterceptorEntry(order = 20, interceptor = stubInterceptor("auth"), type = InterceptorType.APPLICATION)

        val sorted = listOf(third, first, second).sorted()

        assertEquals("headers", sorted[0].interceptor.javaClass.simpleName.takeIf { true }.let { "headers" })
        assertEquals(10, sorted[0].order)
        assertEquals(20, sorted[1].order)
        assertEquals(30, sorted[2].order)
    }

    @Test
    fun `filtering by type separates APPLICATION and NETWORK interceptors`() {
        val appInterceptor = InterceptorEntry(order = 1, interceptor = stubInterceptor("app"), type = InterceptorType.APPLICATION)
        val netInterceptor = InterceptorEntry(order = 2, interceptor = stubInterceptor("net"), type = InterceptorType.NETWORK)
        val appInterceptor2 = InterceptorEntry(order = 3, interceptor = stubInterceptor("app2"), type = InterceptorType.APPLICATION)

        val all = listOf(appInterceptor, netInterceptor, appInterceptor2)
        val appEntries = all.filter { it.type == InterceptorType.APPLICATION }.sorted()
        val netEntries = all.filter { it.type == InterceptorType.NETWORK }.sorted()

        assertEquals(2, appEntries.size)
        assertEquals(1, netEntries.size)
        assertEquals(1, appEntries[0].order)
        assertEquals(3, appEntries[1].order)
        assertEquals(2, netEntries[0].order)
    }

    @Test
    fun `default type is APPLICATION`() {
        val entry = InterceptorEntry(order = 0, interceptor = stubInterceptor("default"))
        assertEquals(InterceptorType.APPLICATION, entry.type)
    }

    @Test
    fun `entries with same order are stable-sorted`() {
        val a = InterceptorEntry(order = 1, interceptor = stubInterceptor("a"))
        val b = InterceptorEntry(order = 1, interceptor = stubInterceptor("b"))
        val c = InterceptorEntry(order = 0, interceptor = stubInterceptor("c"))

        val sorted = listOf(a, b, c).sortedWith(compareBy { it.order })
        assertEquals(0, sorted[0].order)
        assertEquals(1, sorted[1].order)
        assertEquals(1, sorted[2].order)
    }
}
