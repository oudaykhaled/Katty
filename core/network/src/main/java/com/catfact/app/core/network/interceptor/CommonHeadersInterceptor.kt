package com.catfact.app.core.network.interceptor

import com.catfact.app.core.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommonHeadersInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header("Accept", "application/json")
            .header("X-Client-Version", BuildConfig.VERSION_NAME)
            .header("X-Platform", "Android")
            .build()
        return chain.proceed(request)
    }
}
