package com.catfact.app.core.network.di

import com.catfact.app.core.network.interceptor.CommonHeadersInterceptor
import com.catfact.app.core.network.interceptor.InterceptorEntry
import com.catfact.app.core.network.interceptor.RetryInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InterceptorModule {

    @Provides
    @IntoSet
    @Singleton
    fun provideRetryInterceptorEntry(interceptor: RetryInterceptor): InterceptorEntry =
        InterceptorEntry(order = 5, interceptor = interceptor)

    @Provides
    @IntoSet
    @Singleton
    fun provideCommonHeadersInterceptorEntry(interceptor: CommonHeadersInterceptor): InterceptorEntry =
        InterceptorEntry(order = 10, interceptor = interceptor)
}
