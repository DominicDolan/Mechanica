package gl.vbo

import loader.emptyFloatBuffer
import org.lwjgl.opengl.GL20
import java.nio.FloatBuffer

class MutableVBO(override val id: Int, vertexCount: Int, private val attributePointer: AttributePointer) : VBO {
    private var maxVertices = vertexCount
    override var vertexCount = vertexCount
        private set

    override fun bind() {
        GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, id)
        attributePointer.enable()
    }

    fun updateBuffer(floats: FloatBuffer, offset: Long = 0) {
        val cs = attributePointer.coordinateSize
        val vertexCount = floats.remaining()/cs
        if (vertexCount > maxVertices) {
            increaseSize()
        }
        this.vertexCount = vertexCount
        val byteOffset = offset*cs*4

        GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, id)
        GL20.glBufferSubData(GL20.GL_ARRAY_BUFFER, byteOffset, floats)
    }

    fun updateBuffer(floats: FloatArray, offset: Long = 0) {
        val cs = attributePointer.coordinateSize
        val vertexCount = floats.size/cs
        if (vertexCount > maxVertices) {
            increaseSize()
        }
        this.vertexCount = vertexCount
        val byteOffset = offset*cs*4

        GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, id)
        GL20.glBufferSubData(GL20.GL_ARRAY_BUFFER, byteOffset, floats)
    }

    private fun increaseSize() {
        maxVertices *= 2
        val floats = emptyFloatBuffer(maxVertices*attributePointer.coordinateSize)
        GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, id)
        GL20.glBufferData(GL20.GL_ARRAY_BUFFER, floats, GL20.GL_STATIC_DRAW)
    }

}