package com.catfact.app.core.network.interceptor

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CurlLoggingInterceptorTest {

    private lateinit var server: MockWebServer
    private lateinit var client: OkHttpClient

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        client = OkHttpClient.Builder()
            .addInterceptor(CurlLoggingInterceptor())
            .build()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `intercept proceeds and returns response`() {
        server.enqueue(MockResponse().setResponseCode(200).setBody("ok"))

        val response = client.newCall(
            Request.Builder().url(server.url("/")).build()
        ).execute()

        assertEquals(200, response.code)
    }

    @Test
    fun `intercept logs request with headers`() {
        server.enqueue(MockResponse().setResponseCode(200).setBody("ok"))

        val response = client.newCall(
            Request.Builder()
                .url(server.url("/test"))
                .addHeader("Authorization", "Bearer token")
                .addHeader("Accept", "application/json")
                .build()
        ).execute()

        assertEquals(200, response.code)
        assertEquals(1, server.requestCount)
    }
}
