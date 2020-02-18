package gl.vbo

import gl.AttributePointer
import loader.emptyFloatBuffer
import loader.toBuffer
import org.lwjgl.opengl.GL20
import org.lwjgl.system.MemoryUtil
import util.extensions.toFloatBuffer
import util.units.Vector
import java.nio.FloatBuffer

interface VBO {
    val id: Int
    val vertexCount: Int
    fun bind()

    companion object {
        fun create(vertices: FloatBuffer, attributePointer: AttributePointer): VBO {
            return genBuffer(vertices, vertices.remaining() / attributePointer.coordinateSize, attributePointer)
        }

        fun create(vertices: FloatBuffer, vertexCount: Int, attributePointer: AttributePointer): VBO {
            return genBuffer(vertices, vertexCount, attributePointer)
        }

        fun create(vertices: Array<Vector>, attributePointer: AttributePointer): VBO {
            val floats = vertices.toFloatBuffer(attributePointer.coordinateSize)
            return genBuffer(floats, vertices.size, attributePointer)
        }

        fun createMutable(vertexCount: Int, attributePointer: AttributePointer) : MutableVBO {
            val floats = emptyFloatBuffer(vertexCount*attributePointer.coordinateSize)
            val id = GL20.glGenBuffers()
            GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, id)
            GL20.glBufferData(GL20.GL_ARRAY_BUFFER, floats, GL20.GL_STATIC_DRAW)
            return MutableVBO(id, vertexCount, attributePointer)
        }

        fun genBuffer(floats: FloatBuffer, vertexCount: Int, attributePointer: AttributePointer): VBO {
            val id = GL20.glGenBuffers()
            GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, id)
            GL20.glBufferData(GL20.GL_ARRAY_BUFFER, floats, GL20.GL_STATIC_DRAW)
            return AttributeBuffer(id, vertexCount, attributePointer)
        }
    }
}