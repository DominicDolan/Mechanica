package gl.vbo

import loader.contentsToString
import loader.emptyFloatBuffer
import loader.emptyShortBuffer
import org.lwjgl.opengl.GL20
import util.extensions.toFloatBuffer
import util.units.Vector
import java.nio.FloatBuffer
import java.nio.ShortBuffer

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

        fun createIndicesBuffer(indices: ShortBuffer): VBO {
            val size = indices.remaining()
            val id = GL20.glGenBuffers()
            GL20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, id)
            GL20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, indices, GL20.GL_STATIC_DRAW)
            return IndexVBO(id, size)
        }

        fun createMutableIndicesBuffer(vertexCount: Int): MutableIndexVBO {
            val buffer = emptyShortBuffer(vertexCount)
            val id = GL20.glGenBuffers()
            GL20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, id)
            GL20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, buffer, GL20.GL_STATIC_DRAW)
            return MutableIndexVBO(id, vertexCount)
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