package demo.text

import gl.utils.createIndicesArrayForQuads
import gl.utils.positionAttribute
import gl.utils.texCoordsAttribute
import gl.vbo.MutableVBO
import gl.vbo.VBO
import loader.toBuffer
import models.Model
import org.lwjgl.opengl.GL11

class TextModel(private val font: Font) : Model(
        VBO.createMutable(100*4, positionAttribute),
        VBO.createMutable(100*4, texCoordsAttribute),
        VBO.createIndicesBuffer(createIndicesArrayForQuads(100).toBuffer()),
        draw = { model ->
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.atlas.id)
            GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount, GL11.GL_UNSIGNED_SHORT, 0)
        }
) {
    private val positionVBO: MutableVBO
        get() = vbos[0] as MutableVBO
    private val texCoordsVBO: MutableVBO
        get() = vbos[1] as MutableVBO
    var text: String = ""
        set(value) = setVBOs(value)

    private fun setVBOs(text: String) {

        var i = 0
        for (char in text) {
            val c = font.loadChar(char)
            if (char == ' ' || char == '\n') {
                continue
            }
            positionVBO.updateBuffer(c.positions, i * 4L)
            texCoordsVBO.updateBuffer(c.texCoords, i * 4L)
            i++
        }
        font.resetCursor()
        vertexCount = i*6
    }
}