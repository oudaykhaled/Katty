package com.catfact.app.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class CatFactResponse(
    val fact: String,
    val length: Int
)
