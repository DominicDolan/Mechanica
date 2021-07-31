package com.mechanica.engine.util

import kotlin.math.min

class NullTerminatedChars(capacity: Int = 50) {
    val chars = CharArray(capacity) { Character.MIN_VALUE }
    var size: Int = 0

    fun add(char: Char) {
        var index = 0

        while (index < chars.size) {
            if (chars[index] == Character.MIN_VALUE) {
                chars[index] = char
                break
            }
            index++
        }
        size = index+1
    }

    fun addCodePoint(cp: Int) {
        when {
            Character.isBmpCodePoint(cp) -> {
                add(cp.toChar())
            }
            Character.isValidCodePoint(cp) -> {
                add(Character.highSurrogate(cp))
                add(Character.lowSurrogate(cp))
            }
        }
    }

    fun get(other: NullTerminatedChars) {
        get(other.chars)
    }

    fun get(other: CharArray) {
        val endIndex = min(other.size, this.chars.size)
        this.chars.copyInto(other, endIndex = endIndex)
        if (endIndex < other.size) {
            other.fill(Character.MIN_VALUE, endIndex, other.lastIndex)
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
        setEach { Character.MIN_VALUE }
    }

    inline fun forEach(operation: (Char) -> Unit) {
        setEach { operation(it); it }
    }

    inline fun setEach(operation: (Char) -> Char) {
        var i = 0
        var currentChar = chars.first()

        while (currentChar != Character.MIN_VALUE && i < chars.size - 1) {
            chars[i] = operation(currentChar)
            currentChar = chars[++i]
        }
    }
}