package com.mechanica.engine.text

class CharacterCursor {
    var xAdvance = 0f
    var yAdvance = 0f
    var currentLine = 0
    var previousChar = ' '
    var currentChar = ' '
    var charIndex = 0
    var nonWhiteSpaceIndex = 0

    fun advance(c: Char) {
        previousChar = currentChar
        currentChar = c
        if (c == ' ' || c == '\n' || c == '\r') {
            charIndex++
        } else {
            nonWhiteSpaceIndex++
            charIndex++
        }
    }

    fun reset() {
        charIndex = 0
        nonWhiteSpaceIndex = 0
        currentLine = 0
        xAdvance = 0f
        yAdvance = 0f
        previousChar = ' '
        currentChar = ' '
    }
}