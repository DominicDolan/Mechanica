package com.mechanica.engine.util

import kotlin.test.Test
import kotlin.test.assertEquals

class CodePointTest {
    @Test
    fun convertsBmpSupplementaryAndInvalidCodePoints() {
        assertEquals("A", codepointToString(65))
        assertEquals("\uD83D\uDE00", codepointToString(0x1F600))
        assertEquals("?", codepointToString(-1))
        assertEquals("?", codepointToString(0x110000))
        val chars = NullTerminatedChars()
        chars.addCodePoint(0x1F600)
        assertEquals('\uD83D', chars.chars[0])
        assertEquals('\uDE00', chars.chars[1])
    }
}
