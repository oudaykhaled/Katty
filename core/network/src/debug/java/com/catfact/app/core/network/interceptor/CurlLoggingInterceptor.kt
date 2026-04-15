package com.catfact.app.core.network.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurlLoggingInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val curl = buildString {
            append("curl -X ${request.method}")
            request.headers.forEach { (name, value) ->
                append(" -H '$name: $value'")
            }
            append(" '${request.url}'")
        }
        Log.d("CurlLog", curl)
        return chain.proceed(request)
    }
}
