package gl.models

import font.Font
import gl.utils.createIndicesArrayForQuads
import gl.vbo.AttributeArray
import gl.vbo.ElementIndexArray
import gl.vbo.pointer.VBOPointer
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBTTAlignedQuad
import org.lwjgl.system.MemoryStack
import util.extensions.restrain
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
        get() = vbos[0] as AttributeArray
    private val texCoordsAttribute: AttributeArray
        get() = vbos[1] as AttributeArray

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

        MemoryStack.stackPush().use { stack ->
            var i = 0

            val x = stack.floats(0f)
            val y = stack.floats(0f)
            val q = STBTTAlignedQuad.mallocStack(stack)

            var kern = 0f
            var line = 0
            var characterIndex = 0
            for (char in text) {
                if (i != 0) {
                    kern += font.getKernAdvance(text[i-1], char)
                }
                if (char == '\n') {
                    x.put(0, 0f)
                }
                val coords = font.alignedQuadAsFloats(char, q, x, y, kern, -line*font.lineHeight)
                characterIndex++
                characterPositions[characterIndex] = (x[0]*font.scale*font.quadScale).toDouble()
                if (char == ' ') {
                    continue
                } else if (char == '\n') {
                    newLineLocations[line] = characterIndex
                    line++
                    kern = 0f
                    x.put(0, 0f)
                    continue
                }
                positionAttribute.update(coords.positions, i * 4)
                texCoordsAttribute.update(coords.texCoords, i * 4)
                i++
            }
            newLineLocations[line] = text.length + 1
            vertexCount = i*6
        }
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

    fun getCharacterIndex(x: Double, line: Int): Int {
        val restrainedLine = line.restrain(0, lineCount)

        val start = if (restrainedLine == 0) 0 else newLineLocations[restrainedLine-1]
        val end = newLineLocations[restrainedLine] - 1

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



}