package gl.vbo

import loader.emptyShortBuffer
import org.lwjgl.opengl.GL20
import java.nio.ShortBuffer

class MutableIndexVBO(override val id: Int, vertexCount: Int) : VBO {
    private val maxVertices = vertexCount
    override var vertexCount = vertexCount
        private set
    private val buffer = emptyShortBuffer(vertexCount)

    override fun bind() {
        GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, id)
    }

    fun updateBuffer(indices: ShortBuffer) {
        val vertexCount = indices.remaining()
        if (vertexCount > maxVertices) {
            System.err.println("Warning: the number of vertices being set ($vertexCount) is larger than the size of the buffer ($maxVertices)")
        }
        this.vertexCount = vertexCount
        GL20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, id)
        GL20.glBufferSubData(GL20.GL_ELEMENT_ARRAY_BUFFER, 0, indices)
    }

    fun updateBuffer(indices: ShortArray) {
        val vertexCount = indices.size
        if (vertexCount > maxVertices) {
            System.err.println("Warning: the number of vertices being set ($vertexCount) is larger than the size of the buffer ($maxVertices)")
        }
        this.vertexCount = vertexCount
        GL20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, id)
        GL20.glBufferSubData(GL20.GL_ELEMENT_ARRAY_BUFFER, 0, indices)
    }
}