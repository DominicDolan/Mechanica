package com.mechanica.engine.text

import com.mechanica.engine.context.loader.GLLoader
import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.util.extensions.constrain
import kotlin.math.abs

class Text(text: String, val font: Font = GLLoader.fontLoader.defaultFont) {

    var text = text
        set(value) {
            field = value
            sentence.update(value, font)
        }

    private val sentence = SentenceCoordinates(text, font)

    val positions: Array<DynamicVector>
        get() = sentence.positions
    val texCoords: Array<DynamicVector>
        get() = sentence.texCoords
    val vertexCount: Int
        get() = sentence.vertexCount

    val lineCount: Int
        get() = sentence.lineCount

    fun getLine(index: Int): Int {
        var line = 0
        while (rcl(line) != -1) {
            if (rcl(line) <= index) {
                if (rcl(line+1)==-1) break
                line++
            } else {
                break
            }
        }
        return line
    }

    fun getClosestCharacterPosition(x: Double, line: Int): Double {
        val index = sentence.searchCharacter(x, line)
        return cp(index)
    }

    fun getEndOfLinePosition(line: Int): Double {
        val characterIndex = rcl(line)-1
        return if (characterIndex > -1) {
            cp(characterIndex)
        } else 0.0
    }

    fun search(x: Double, line: Int) = sentence.searchCharacter(x, line)


    fun getCharacterPosition(index: Int) = sentence.characterPosition(index)

    /**
     * returns the carriage return character location in sentence
     */
    private fun rcl(i: Int) = sentence.returnCharacterIndex(i)
    
    /**
     * returns the character x position in sentence
     */
    private fun cp(i: Int) = sentence.characterPosition(i)
}