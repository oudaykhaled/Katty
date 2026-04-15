package com.catfact.app.core.domain.model

import java.io.IOException

fun Throwable.toErrorKind(): ErrorKind = when (this) {
    is IOException -> ErrorKind.Network(message ?: "Network error")
    is ServerException -> ErrorKind.Server(code, message ?: "Server error")
    else -> ErrorKind.Unknown(message ?: "Unknown error")
}
