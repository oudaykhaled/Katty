package com.catfact.app.core.network.di

import com.catfact.app.core.network.BuildConfig
import com.catfact.app.core.network.api.CatFactApi
import com.catfact.app.core.network.interceptor.InterceptorEntry
import com.catfact.app.core.network.interceptor.InterceptorType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptors: Set<@JvmSuppressWildcards InterceptorEntry>): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        val sorted = interceptors.sorted()
        sorted.filter { it.type == InterceptorType.APPLICATION }.forEach {
            builder.addInterceptor(it.interceptor)
        }
        sorted.filter { it.type == InterceptorType.NETWORK }.forEach {
            builder.addNetworkInterceptor(it.interceptor)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideCatFactApi(retrofit: Retrofit): CatFactApi =
        retrofit.create(CatFactApi::class.java)
}
