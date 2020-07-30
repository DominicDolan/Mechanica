package com.mechanica.engine.input

import com.mechanica.engine.context.callbacks.KeyboardHandler
import com.mechanica.engine.util.NullTerminatedChars

object TextInput {

    @PublishedApi
    internal val codepoints = NullTerminatedChars(10)
    var textInputCount: Int = 0
        private set
    val hasTextInput: Boolean
        get() = textInputCount != 0

    internal fun prepare() {
        codepoints.clear()
        textInputCount = 0
    }

    fun getCodepoints(sb: StringBuilder, offset: Int = -1): Int {
        return codepoints.get(sb, offset)
    }

    fun getCodepoints(charArray: CharArray) {
        codepoints.get(charArray)
    }

    fun getCodepoints(chars: NullTerminatedChars) {
        codepoints.get(chars)
    }

    internal fun addCodePoint(cp: Int) {
        codepoints.addCodePoint(cp)
        textInputCount++
    }

    inline fun forEachChar(operation: (Char) -> Unit) {
        codepoints.forEach(operation)
    }

}