package com.catfact.app.core.network.interceptor

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RetryInterceptorTest {

    private lateinit var server: MockWebServer
    private lateinit var client: OkHttpClient

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        client = OkHttpClient.Builder()
            .addInterceptor(RetryInterceptor())
            .build()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `successful request does not retry`() {
        server.enqueue(MockResponse().setResponseCode(200).setBody("ok"))

        val response = client.newCall(
            Request.Builder().url(server.url("/")).build()
        ).execute()

        assertEquals(200, response.code)
        assertEquals(1, server.requestCount)
    }

    @Test
    fun `retries on 500 and succeeds`() {
        server.enqueue(MockResponse().setResponseCode(500))
        server.enqueue(MockResponse().setResponseCode(200).setBody("ok"))

        val response = client.newCall(
            Request.Builder().url(server.url("/")).build()
        ).execute()

        assertEquals(200, response.code)
        assertEquals(2, server.requestCount)
    }

    @Test
    fun `does not retry on 4xx`() {
        server.enqueue(MockResponse().setResponseCode(404))

        val response = client.newCall(
            Request.Builder().url(server.url("/")).build()
        ).execute()

        assertEquals(404, response.code)
        assertEquals(1, server.requestCount)
    }

    @Test
    fun `exhausts retries on persistent 500`() {
        server.enqueue(MockResponse().setResponseCode(500))
        server.enqueue(MockResponse().setResponseCode(500))
        server.enqueue(MockResponse().setResponseCode(500))

        val response = client.newCall(
            Request.Builder().url(server.url("/")).build()
        ).execute()

        assertEquals(500, response.code)
        assertEquals(3, server.requestCount)
    }

    @Test
    fun `retries on IOException and eventually succeeds`() {
        server.enqueue(MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AT_START))
        server.enqueue(MockResponse().setResponseCode(200).setBody("recovered"))

        val shortTimeoutClient = OkHttpClient.Builder()
            .addInterceptor(RetryInterceptor())
            .connectTimeout(1, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(1, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        val response = shortTimeoutClient.newCall(
            Request.Builder().url(server.url("/")).build()
        ).execute()

        assertEquals(200, response.code)
    }

    @Test(expected = IllegalStateException::class)
    fun `programming failures propagate without retry`() {
        val throwingInterceptor = okhttp3.Interceptor {
            throw IllegalStateException("Unexpected failure")
        }

        val clientWithThrowingInterceptor = OkHttpClient.Builder()
            .addInterceptor(RetryInterceptor())
            .addInterceptor(throwingInterceptor)
            .build()

        clientWithThrowingInterceptor.newCall(
            Request.Builder().url(server.url("/")).build()
        ).execute()
    }

    @Test
    fun `write requests are never automatically replayed`() {
        server.enqueue(MockResponse().setResponseCode(503))
        val request = Request.Builder().url(server.url("/"))
            .post("payload".toRequestBody())
            .build()

        client.newCall(request).execute().use { response ->
            assertEquals(503, response.code)
        }
        assertEquals(1, server.requestCount)
    }

    @Test
    fun `cancelled requests are not retried`() {
        var attempts = 0
        val cancellingClient = client.newBuilder().addInterceptor { chain ->
            attempts++
            chain.call().cancel()
            throw java.io.IOException("Cancelled")
        }.build()

        org.junit.Assert.assertThrows(java.io.IOException::class.java) {
            cancellingClient.newCall(Request.Builder().url(server.url("/")).build()).execute()
        }
        assertEquals(1, attempts)
    }

    @Test(expected = java.io.IOException::class)
    fun `throws IOException when all retries exhausted by connection failures`() {
        repeat(3) {
            server.enqueue(MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AT_START))
        }

        val shortTimeoutClient = OkHttpClient.Builder()
            .addInterceptor(RetryInterceptor())
            .connectTimeout(1, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(1, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        shortTimeoutClient.newCall(
            Request.Builder().url(server.url("/")).build()
        ).execute()
    }
}
