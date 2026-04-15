package com.catfact.app.core.network.interceptor

import okhttp3.OkHttpClient
import okhttp3.Request
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

    @Test(expected = java.io.IOException::class)
    fun `wraps non-IOException as IOException after retries exhausted`() {
        val throwingInterceptor = okhttp3.Interceptor { chain ->
            throw RuntimeException("Unexpected failure")
        }

        val clientWithThrowingInterceptor = OkHttpClient.Builder()
            .addInterceptor(RetryInterceptor())
            .addInterceptor(throwingInterceptor)
            .build()

        clientWithThrowingInterceptor.newCall(
            Request.Builder().url(server.url("/")).build()
        ).execute()
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
