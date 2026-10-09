package com.catfact.app.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetryInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        // Automatic replay is safe only for the read-only requests used by this app.
        if (request.method !in setOf("GET", "HEAD")) return chain.proceed(request)
        var lastException: IOException? = null

        repeat(MAX_RETRIES + 1) { attempt ->
            try {
                val response = chain.proceed(request)
                if (response.isSuccessful || response.code < SERVER_ERROR_START) return response
                if (attempt < MAX_RETRIES) {
                    response.close()
                    pauseBeforeRetry(chain, attempt)
                } else {
                    return response
                }
            } catch (e: IOException) {
                if (chain.call().isCanceled() || Thread.currentThread().isInterrupted) throw e
                lastException = e
                if (attempt < MAX_RETRIES) {
                    pauseBeforeRetry(chain, attempt)
                }
            }
        }

        throw lastException ?: IOException("Retry exhausted")
    }

    private fun backoffMillis(attempt: Int): Long = INITIAL_BACKOFF_MS * (1L shl attempt)

    private fun pauseBeforeRetry(chain: Interceptor.Chain, attempt: Int) {
        if (chain.call().isCanceled()) throw IOException("Request cancelled")
        try {
            Thread.sleep(backoffMillis(attempt))
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IOException("Retry interrupted", e)
        }
    }

    companion object {
        private const val MAX_RETRIES = 2
        private const val INITIAL_BACKOFF_MS = 500L
        private const val SERVER_ERROR_START = 500
    }
}
