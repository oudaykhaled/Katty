package com.catfact.app

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DeepLinkParsingTest {

    @Test
    fun `valid deep link returns fact id`() {
        val uri = Uri.parse("catfact://fact/abc-123")
        assertEquals("abc-123", parseDeepLinkUri(uri))
    }

    @Test
    fun `null uri returns null`() {
        assertNull(parseDeepLinkUri(null))
    }

    @Test
    fun `wrong scheme returns null`() {
        val uri = Uri.parse("https://fact/abc-123")
        assertNull(parseDeepLinkUri(uri))
    }

    @Test
    fun `wrong host returns null`() {
        val uri = Uri.parse("catfact://facts/abc-123")
        assertNull(parseDeepLinkUri(uri))
    }

    @Test
    fun `no path segment returns null`() {
        val uri = Uri.parse("catfact://fact")
        assertNull(parseDeepLinkUri(uri))
    }
}
