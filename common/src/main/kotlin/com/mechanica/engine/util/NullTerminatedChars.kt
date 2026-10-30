package com.mechanica.engine.util

import kotlin.math.min

class NullTerminatedChars(capacity: Int = 50) {
    val chars = CharArray(capacity) { '\u0000' }
    var size: Int = 0

    fun add(char: Char) {
        var index = 0

        while (index < chars.size) {
            if (chars[index] == '\u0000') {
                chars[index] = char
                break
            }
            index++
        }
        size = index+1
    }

    fun addCodePoint(cp: Int) {
        if (cp in 0..0x10FFFF) codepointToString(cp).forEach { add(it) }
    }

    fun get(other: NullTerminatedChars) {
        get(other.chars)
    }

    fun get(other: CharArray) {
        val endIndex = min(other.size, this.chars.size)
        this.chars.copyInto(other, endIndex = endIndex)
        if (endIndex < other.size) {
            other.fill('\u0000', endIndex, other.lastIndex)
        }
    }

    fun get(sb: StringBuilder, offset: Int = -1): Int {
        var count = 0
        forEach {
            if (offset < 0) {
                sb.append(it)
            } else {
                sb.insert(offset+count, it)
            }
            count++
        }
        return count
    }

    fun clear() {
        size = 0
        setEach { '\u0000' }
    }

    inline fun forEach(operation: (Char) -> Unit) {
        setEach { operation(it); it }
    }

    inline fun setEach(operation: (Char) -> Char) {
        var i = 0
        var currentChar = chars.first()

        while (currentChar != '\u0000' && i < chars.size - 1) {
            chars[i] = operation(currentChar)
            currentChar = chars[++i]
        }
    }
}