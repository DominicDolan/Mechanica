package gl.vbo.pointer

import org.lwjgl.opengl.GL20

interface VBOPointer {
    val coordinateSize: Int
    val bufferType: Int
    val variableType: Int
    val variableSize: Int

    companion object {

        val position = AttributePointer.create(0, 3)
        val texCoords = AttributePointer.create(1, 2)

        val elementArrayPointer = object : VBOPointer {
            override val coordinateSize: Int = 1
            override val bufferType: Int = GL20.GL_ELEMENT_ARRAY_BUFFER
            override val variableType: Int = GL20.GL_SHORT
            override val variableSize: Int = 2

        }
    }
}