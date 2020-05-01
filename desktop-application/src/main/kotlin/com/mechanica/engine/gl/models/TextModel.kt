package com.mechanica.engine.gl.models

import com.mechanica.engine.font.Font
import com.mechanica.engine.gl.utils.createIndicesArrayForQuads
import com.mechanica.engine.gl.vbo.AttributeArray
import com.mechanica.engine.gl.vbo.ElementIndexArray
import com.mechanica.engine.gl.vbo.pointer.VBOPointer
import com.mechanica.engine.util.extensions.constrain
import org.lwjgl.opengl.GL11
import kotlin.math.abs

class TextModel(private val font: Font) : Model(
        AttributeArray(100*4, VBOPointer.position),
        AttributeArray(100*4, VBOPointer.texCoords),
        ElementIndexArray(createIndicesArrayForQuads(1000)),
        draw = { model ->
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.atlas.id)
            GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_SHORT, 0)
        }
) {
    private val positionAttribute: AttributeArray
        get() = inputs[0] as AttributeArray
    private val texCoordsAttribute: AttributeArray
        get() = inputs[1] as AttributeArray

    private var characterPositions = DoubleArray(100)
    private var newLineLocations = IntArray(100) { -1 }
    var lineCount = 0

    var text: String = ""
        set(value) {
            field = value
            setVBOs(value)
        }

    private fun setVBOs(text: String) {
        characterPositions.fill(0.0)
        newLineLocations.fill(-1)
        lineCount = text.count { '\n' == it }
        checkArraySizes(text)

        loopCharacters(text)
    }

    private fun loopCharacters(text: String) {
        var i = 0

        var line = 0
        var characterIndex = 0
        var coords = font.resetCharacterCoordinates()
        for (char in text) {

            characterIndex++
            if (char.isLineSeparator()) {
                if (char == '\r') continue
                coords.xAdvance = 0f
                newLineLocations[line] = characterIndex
                line++
                coords.xAdvance = 0f
                coords.yAdvance = -line*font.lineHeight
                continue
            }

            coords = font.alignedQuadAsFloats(char, coords)

            characterPositions[characterIndex] = (coords.xAdvance * font.scale * font.quadScale).toDouble()
            if (char == ' ') {
                continue
            }

            positionAttribute.update(coords.positions, i * 4)
            texCoordsAttribute.update(coords.texCoords, i * 4)

            i++
        }
        newLineLocations[line] = text.length + 1
        vertexCount = i*6
    }

    fun getLine(index: Int): Int {
        val nll = newLineLocations
        var line = 0
        while (nll[line] != -1) {
            if (nll[line] <= index) {
                if (nll[line+1]==-1) break
                line++
            } else {
                break
            }
        }
        return line
    }

    fun getClosestCharacterPosition(x: Double, line: Int): Double {
        val index = getCharacterIndex(x, line)
        return characterPositions[index]
    }

    fun getEndOfLinePosition(line: Int): Double {
        val characterIndex = newLineLocations[line]-1
        return if (characterIndex > -1) {
            characterPositions[characterIndex]
        } else 0.0
    }

    fun getCharacterIndex(x: Double, line: Int): Int {
        val constrainedLine = line.constrain(0, lineCount)

        val start = if (constrainedLine == 0) 0 else newLineLocations[constrainedLine-1]
        val end = newLineLocations[constrainedLine] - 1

        val search = characterPositions.binarySearch(x, start, end)

        return  if (search == 0) 0
                else abs(search) - 1
    }

    fun getCharacterPosition(index: Int) = characterPositions[index]

    private fun checkArraySizes(text: String) {
        characterPositions = checkArraySize(text.length + 1, characterPositions)
        val newLines = text.count { it == '\n' }
        newLineLocations = checkArraySize(newLines, newLineLocations)
    }

    private fun checkArraySize(requiredSize: Int, array: DoubleArray): DoubleArray {
        var size = array.size
        if (size < requiredSize) {
            while (size < requiredSize) {
                size *= 2
            }
            return DoubleArray(size)
        }
        return array
    }

    private fun checkArraySize(requiredSize: Int, array: IntArray): IntArray {
        var size = array.size
        if (size < requiredSize) {
            while (size < requiredSize) {
                size *= 2
            }
            return IntArray(size)
        }
        return array
    }

    private fun Char.isLineSeparator(): Boolean {
        for (c in LINE_SEPARATOR) {
            if (this == c) return true
        }
        return false
    }

    companion object {
        private val LINE_SEPARATOR = System.getProperty("line.separator")
    }
}