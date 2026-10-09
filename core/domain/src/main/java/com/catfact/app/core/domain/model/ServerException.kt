package com.catfact.app.core.domain.model

class ServerException(val code: Int, message: String, cause: Throwable? = null) : Exception(message, cause)
