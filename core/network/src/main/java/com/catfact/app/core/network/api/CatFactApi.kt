package com.catfact.app.core.network.api

import com.catfact.app.core.network.model.CatFactListResponse
import com.catfact.app.core.network.model.CatFactResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CatFactApi {
    @GET("fact")
    suspend fun getRandomFact(): CatFactResponse

    @GET("facts")
    suspend fun getFacts(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): CatFactListResponse
}
