package demo.text

import gl.utils.createIndicesArrayForQuads
import gl.vbo.AttributeArray
import gl.vbo.ElementIndexArray
import gl.vbo.pointer.VBOPointer
import models.Model
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBTTAlignedQuad
import org.lwjgl.system.MemoryStack

class TextModel(private val font: Font) : Model(
        AttributeArray(100*4, VBOPointer.position),
        AttributeArray(100*4, VBOPointer.texCoords),
        ElementIndexArray(createIndicesArrayForQuads(1000)),
        draw = { model ->
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.atlas.id)
            GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_SHORT, 0)
        }
) {
    private val positionVBO: AttributeArray
        get() = vbos[0] as AttributeArray
    private val texCoordsVBO: AttributeArray
        get() = vbos[1] as AttributeArray
    var text: String = ""
        set(value) = setVBOs(value)

    private fun setVBOs(text: String) {

        MemoryStack.stackPush().use { stack ->
            var i = 0

            val x = stack.floats(0f)
            val y = stack.floats(0f)
            val q = STBTTAlignedQuad.mallocStack(stack)

            var kern = 0f
            var line = 0
            for (char in text) {
                if (i != 0) {
                    kern += font.getKernAdvance(text[i-1], char)
                }
                val coords = font.alignedQuadAsFloats(char, q, x, y, kern, -line*font.lineHeight)
                if (char == ' ') {
                    continue
                } else if (char == '\n') {
                    line++
                    kern = 0f
                    x.put(0, 0f)
                    continue
                }
                positionVBO.update(coords.positions, i * 4)
                texCoordsVBO.update(coords.texCoords, i * 4)
                i++
            }
            vertexCount = i*6
        }
    }

}