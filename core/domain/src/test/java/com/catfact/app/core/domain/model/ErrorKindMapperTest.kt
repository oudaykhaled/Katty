package com.catfact.app.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class ErrorKindMapperTest {

    @Test
    fun `IOException maps to ErrorKind Network`() {
        val error = IOException("Connection timed out").toErrorKind()

        assertTrue(error is ErrorKind.Network)
        assertEquals("Connection timed out", (error as ErrorKind.Network).message)
    }

    @Test
    fun `IOException with null message uses default`() {
        val error = IOException().toErrorKind()

        assertTrue(error is ErrorKind.Network)
        assertEquals("Network error", (error as ErrorKind.Network).message)
    }

    @Test
    fun `RuntimeException maps to ErrorKind Unknown`() {
        val error = RuntimeException("Something broke").toErrorKind()

        assertTrue(error is ErrorKind.Unknown)
        assertEquals("Something broke", (error as ErrorKind.Unknown).message)
    }

    @Test
    fun `IllegalStateException maps to ErrorKind Unknown`() {
        val error = IllegalStateException("Bad state").toErrorKind()

        assertTrue(error is ErrorKind.Unknown)
        assertEquals("Bad state", (error as ErrorKind.Unknown).message)
    }

    @Test
    fun `exception with null message uses default`() {
        val error = RuntimeException().toErrorKind()

        assertTrue(error is ErrorKind.Unknown)
        assertEquals("Unknown error", (error as ErrorKind.Unknown).message)
    }
}
