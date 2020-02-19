package gl.vbo

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20

class AttributePointer private constructor(val index: Int, val coordinateSize: Int) {
    fun enable() {
        GL20.glVertexAttribPointer(index, coordinateSize, GL11.GL_FLOAT, false, 0, 0)
        GL20.glEnableVertexAttribArray(index)
    }

    override fun toString(): String {
        return "Index: $index, size: $coordinateSize"
    }

    companion object {
        private val indicesMap = Array<AttributePointer?>(100){ null }

        fun create(index: Int, coordinateSize: Int): AttributePointer {
            val pointer = indicesMap[index]
            return if (pointer == null) {
                val new = AttributePointer(index, coordinateSize)
                indicesMap[index] = new
                new
            } else {
                System.err.println("An attribute pointer with this index already exists, returning that one instead")
                pointer
            }
        }
    }
}