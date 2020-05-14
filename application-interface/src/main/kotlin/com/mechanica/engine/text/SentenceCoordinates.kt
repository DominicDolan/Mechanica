package com.mechanica.engine.text

import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.util.extensions.constrain
import kotlin.math.abs
import kotlin.math.max

class SentenceCoordinates(text: String, font: Font) {

    var positions: Array<DynamicVector> = Array(text.length*8){ DynamicVector.create() }
        private set
    var texCoords: Array<DynamicVector> = Array(text.length*8){ DynamicVector.create() }
        private set


    private var characterPositions = DoubleArray(100)
    private var returnLocations = IntArray(100) { -1 }

    var lineCount = 0
        private set
    var stringLength = text.length
        private set
    var vertexCount = stringLength*6
        private set

    private val characterCursor = CharacterCursor()

    init {
        update(text, font)
    }

    fun characterPosition(charIndex: Int): Double {
        return characterPositions[charIndex.constrain(0, stringLength)]
    }
    fun returnCharacterIndex(i: Int) = returnLocations[i.constrain(0, lineCount)]

    fun searchCharacter(x: Double, line: Int): Int {
        val start = returnCharacterIndex(line-1)
        val end = max(returnCharacterIndex(line) - 1, start)

        val search = characterPositions.binarySearch(x, start, end)

        return if (search == 0) 0
        else abs(search) - 1
    }

    fun update(text: String, font: Font) {
        checkArrays(text)

        if (text.isNotEmpty())
        for (i in text.indices) {
            addCharacterData(i, text[i], font)
        }

        finalizeData()
    }

    private fun checkArrays(text: String) {
        val length = text.length
        if (length*8 >= positions.size) {
            increaseSize(text.length*12)
        }

        if (length >= characterPositions.size) {
            characterPositions = DoubleArray(length*2)
        }
    }

    private fun addCharacterData(i: Int, c: Char, font: Font) {
        val cursor = characterCursor
        cursor.advance(c)

        if (c.isLineSeparator()) {
            if (c == '\r') return
            addNewLine(i, font.lineHeight)
            return
        }

        font.addCharacterDataToArrays(cursor, positions, texCoords)

        characterPositions[cursor.charIndex] = cursor.xAdvance.toDouble()
    }

    private fun finalizeData() {
        returnLocations[characterCursor.currentLine] = characterCursor.charIndex + 2
        lineCount = characterCursor.currentLine + 1
        vertexCount = characterCursor.nonWhiteSpaceIndex*6
        stringLength = characterCursor.charIndex
        characterCursor.reset()
    }

    private fun addNewLine(i: Int, lineHeight: Float) {
        val cursor = characterCursor
        val line = ++cursor.currentLine
        returnLocations[line-1] = i
        if (line >= returnLocations.size) {
            increaseNewLineArraySize(line*2)
        }

        cursor.xAdvance = 0f
        cursor.yAdvance = -line * lineHeight
    }

    private fun Char.isLineSeparator(): Boolean {
        for (c in LINE_SEPARATOR) {
            if (this == c) return true
        }
        return false
    }

    private fun increaseSize(newSize: Int) {
        positions = Array(newSize){ DynamicVector.create() }
        texCoords = Array(newSize){ DynamicVector.create() }
    }

    private fun increaseNewLineArraySize(newSize: Int) {
        val nll = returnLocations
        returnLocations = IntArray(newSize) { if (it < nll.size) nll[it] else -1 }
    }

    companion object {
        private val LINE_SEPARATOR = System.getProperty("line.separator")
    }

}