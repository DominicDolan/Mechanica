package gl.vbo

import org.lwjgl.opengl.GL20
import java.nio.FloatBuffer

class MutableVBO(override val id: Int, vertexCount: Int, private val attributePointer: AttributePointer) : VBO {
    private val maxVertices = vertexCount
    override var vertexCount = vertexCount
        private set

    override fun bind() {
        GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, id)
        attributePointer.enable()
    }

    fun updateBuffer(floats: FloatBuffer) {
        val vertexCount = floats.remaining()/attributePointer.coordinateSize
        if (vertexCount > maxVertices) {
            System.err.println("Warning: the number of vertices being set ($vertexCount) is larger than the size of the buffer ($maxVertices)")
        }
        this.vertexCount = vertexCount
        GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, id)
        GL20.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, floats)
    }

    fun updateBuffer(floats: FloatArray) {
        val vertexCount = floats.size/attributePointer.coordinateSize
        if (vertexCount > maxVertices) {
            System.err.println("Warning: the number of vertices being set ($vertexCount) is larger than the size of the buffer ($maxVertices)")
        }
        this.vertexCount = vertexCount
        GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, id)
        GL20.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, floats)
    }

}