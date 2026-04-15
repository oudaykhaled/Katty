package com.catfact.app.core.domain.model

sealed interface ErrorKind {
    data class Network(val message: String) : ErrorKind
    data class Server(val code: Int, val message: String) : ErrorKind
    data class Unknown(val message: String) : ErrorKind
}
