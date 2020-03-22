package gl.vbo.pointer

import display.Game
import org.lwjgl.opengl.GL20

class AttributePointer private constructor(
        val index: Int,
        override val coordinateSize: Int,
        override val bufferType: Int,
        override val type: Int) : VBOPointer {

    fun enable() {
        GL20.glVertexAttribPointer(index, coordinateSize, type, false, 0, 0)
        GL20.glEnableVertexAttribArray(index)
    }

    override fun toString(): String {
        return "Index: $index, size: $coordinateSize"
    }

    companion object {

        private val indicesMap = Array<AttributePointer?>(100){ null }

        fun create(index: Int, coordinateSize: Int, bufferType: Int = GL20.GL_ARRAY_BUFFER, type: Int = GL20.GL_FLOAT): AttributePointer {
            val pointer = indicesMap[index]
            return if (pointer == null) {
                val new = AttributePointer(index, coordinateSize, bufferType, type)
                indicesMap[index] = new
                new
            } else {
                val msg = "An attribute pointer with this index already exists, returning that one instead, Attribute: $this"
                if (Game.debug) {
                    throw IllegalStateException(msg)
                } else {
                    System.err.println(msg)
                }
                pointer
            }
        }
    }
}