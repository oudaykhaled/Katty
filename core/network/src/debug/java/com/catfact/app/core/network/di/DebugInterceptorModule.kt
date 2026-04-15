package com.catfact.app.core.network.di

import com.catfact.app.core.network.interceptor.CurlLoggingInterceptor
import com.catfact.app.core.network.interceptor.InterceptorEntry
import com.catfact.app.core.network.interceptor.InterceptorType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DebugInterceptorModule {
    @Provides
    @IntoSet
    @Singleton
    fun provideCurlLoggingEntry(interceptor: CurlLoggingInterceptor): InterceptorEntry =
        InterceptorEntry(order = 99, interceptor = interceptor, type = InterceptorType.NETWORK)
}
