package com.catfact.app.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CatFactListResponse(
    @SerialName("current_page") val currentPage: Int,
    val data: List<CatFactResponse>,
    @SerialName("last_page") val lastPage: Int,
    val total: Int
)
