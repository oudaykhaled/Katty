package com.catfact.app.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class ErrorKindTest {

    @Test
    fun `IOException maps to Network`() {
        val error = IOException("timeout").toErrorKind()
        assertTrue(error is ErrorKind.Network)
        assertEquals("timeout", (error as ErrorKind.Network).message)
    }

    @Test
    fun `IOException with null message uses fallback`() {
        val error = IOException().toErrorKind()
        assertTrue(error is ErrorKind.Network)
        assertEquals("Network error", (error as ErrorKind.Network).message)
    }

    @Test
    fun `ServerException maps to Server with code`() {
        val error = ServerException(500, "Internal Server Error").toErrorKind()
        assertTrue(error is ErrorKind.Server)
        val server = error as ErrorKind.Server
        assertEquals(500, server.code)
        assertEquals("Internal Server Error", server.message)
    }

    @Test
    fun `ServerException 404 maps to Server`() {
        val error = ServerException(404, "Not Found").toErrorKind()
        assertTrue(error is ErrorKind.Server)
        assertEquals(404, (error as ErrorKind.Server).code)
    }

    @Test
    fun `generic RuntimeException maps to Unknown`() {
        val error = RuntimeException("something went wrong").toErrorKind()
        assertTrue(error is ErrorKind.Unknown)
        assertEquals("something went wrong", (error as ErrorKind.Unknown).message)
    }

    @Test
    fun `IllegalStateException maps to Unknown`() {
        val error = IllegalStateException("bad state").toErrorKind()
        assertTrue(error is ErrorKind.Unknown)
    }

    @Test
    fun `exception with null message uses fallback`() {
        val error = RuntimeException().toErrorKind()
        assertTrue(error is ErrorKind.Unknown)
        assertEquals("Unknown error", (error as ErrorKind.Unknown).message)
    }
}
