package gl.vbo.pointer

import game.Game
import org.lwjgl.opengl.GL20

class AttributePointer private constructor(
        val index: Int,
        override val coordinateSize: Int,
        override val bufferType: Int,
        override val variableType: Int,
        override val variableSize: Int) : VBOPointer {

    fun enable() {
        GL20.glVertexAttribPointer(index, coordinateSize, variableType, false, 0, 0)
        GL20.glEnableVertexAttribArray(index)
    }

    override fun toString(): String {
        return "Index: $index, size: $coordinateSize"
    }

    companion object {

        private val indicesMap = Array<AttributePointer?>(100){ null }

        fun create(
                index: Int, coordinateSize: Int,
                bufferType: Int = GL20.GL_ARRAY_BUFFER,
                type: Int = GL20.GL_FLOAT,
                variableSize: Int = 4): AttributePointer {

            val pointer = indicesMap[index]
            return if (pointer == null) {
                val new = AttributePointer(index, coordinateSize, bufferType, type, variableSize)
                indicesMap[index] = new
                new
            } else {
                if (Game.debug.printWarnings) {
                    val msg = "An attribute pointer with this index already exists, returning that one instead, Attribute: $this"
                    System.err.println(msg)
                }
                pointer
            }
        }
    }
}