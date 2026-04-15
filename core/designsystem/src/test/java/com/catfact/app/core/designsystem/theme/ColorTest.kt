package com.catfact.app.core.designsystem.theme

import org.junit.Assert.assertNotEquals
import org.junit.Test

class ColorTest {

    @Test
    fun `primary tones are distinct across palette`() {
        assertNotEquals(Primary40, Secondary40)
        assertNotEquals(Primary40, Tertiary40)
        assertNotEquals(Secondary40, Tertiary40)
    }

    @Test
    fun `light and dark primary tones differ`() {
        assertNotEquals(Primary40, Primary80)
        assertNotEquals(Primary10, Primary90)
    }

    @Test
    fun `neutral extremes are distinct`() {
        assertNotEquals(Neutral0, Neutral100)
        assertNotEquals(Neutral6, Neutral99)
        assertNotEquals(Neutral10, Neutral90)
    }

    @Test
    fun `error tones are distinct from primary`() {
        assertNotEquals(Error40, Primary40)
        assertNotEquals(Error80, Primary80)
    }

    @Test
    fun `neutral variant tones are distinct from neutral`() {
        assertNotEquals(NeutralVariant30, Neutral20)
        assertNotEquals(NeutralVariant90, Neutral90)
    }
}
