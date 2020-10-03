package com.mechanica.engine.text

import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.util.extensions.constrain
import kotlin.math.abs
import kotlin.math.max

class Text(text: String, font: Font = Font.defaultFont) {

    var string = text
        set(value) {
            field = value
            update(value, font)
        }
    var font = font
        set(value) {
            field = value
            update(string, font)
        }

    var positions: Array<DynamicVector> = Array(text.length*8){ DynamicVector.create() }
        private set
    var texCoords: Array<DynamicVector> = Array(text.length*8){ DynamicVector.create() }
        private set

    private var characterPositions = DoubleArray(100)
    private var carriageReturns = IntArray(100) { -1 }

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

    fun getLine(index: Int): Int {
        var line = 0
        while (carriageReturns[line] != -1) {
            if (carriageReturns[line] <= index) {
                if (carriageReturns[line+1]==-1) break
                line++
            } else {
                break
            }
        }
        return line
    }

    fun getCharacterPosition(index: Int) = characterPositions[index]

    fun getClosestCharacterPosition(x: Double, line: Int): Double {
        val index = searchCharacter(x, line)
        return characterPositions[index]
    }

    fun getEndOfLinePosition(line: Int): Double {
        val characterIndex = carriageReturns[line]-1
        return if (characterIndex > -1) {
            characterPositions[characterIndex]
        } else 0.0
    }

    fun carriageReturnIndex(i: Int) = if (i < 0) 0 else carriageReturns[i.constrain(0, lineCount)]

    fun searchCharacter(x: Double, line: Int): Int {
        val start = carriageReturnIndex(line-1)
        val end = max(carriageReturnIndex(line) - 1, start)

        val search = characterPositions.binarySearch(x, start, end)

        return if (search == 0) 0
        else abs(search) - 1
    }

    fun update(text: String, font: Font) {
        checkArrays(text)

        if (text.isNotEmpty())
            for (c in text) {
                addCharacterData(c, font)
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

    private fun addCharacterData(c: Char, font: Font) {
        val cursor = characterCursor
        cursor.advance(c)
        if (c.isLineSeparator()) {
            if (c == '\r') return
            addNewLine(font.lineHeight)
            return
        }

        font.addCharacterDataToArrays(cursor, positions, texCoords)

        characterPositions[cursor.charIndex] = cursor.xAdvance.toDouble()
    }

    private fun finalizeData() {
        carriageReturns[characterCursor.currentLine] = characterCursor.charIndex + 1
        lineCount = characterCursor.currentLine + 1
        vertexCount = characterCursor.nonWhiteSpaceIndex*6
        stringLength = characterCursor.charIndex
        characterCursor.reset()
    }

    private fun addNewLine(lineHeight: Float) {
        val cursor = characterCursor
        val line = ++cursor.currentLine
        carriageReturns[line-1] = cursor.charIndex
        if (line >= carriageReturns.size) {
            increaseNewLineArraySize(line*2)
        }

        cursor.xAdvance = 0f
        cursor.yAdvance = -line * lineHeight
        characterPositions[cursor.charIndex] = cursor.xAdvance.toDouble()
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
        val nll = carriageReturns
        carriageReturns = IntArray(newSize) { if (it < nll.size) nll[it] else -1 }
    }

    companion object {
        private val LINE_SEPARATOR = System.getProperty("line.separator")
    }
}