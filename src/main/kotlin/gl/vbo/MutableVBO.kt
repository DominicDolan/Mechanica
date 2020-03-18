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

    fun updateBuffer(floats: FloatBuffer) {
        val vertexCount = floats.remaining()/attributePointer.coordinateSize
        if (vertexCount > maxVertices) {
            increaseSize()
        }
        this.vertexCount = vertexCount
        GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, id)
        GL20.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, floats)
    }

    fun updateBuffer(floats: FloatArray) {
        val vertexCount = floats.size/attributePointer.coordinateSize
        if (vertexCount > maxVertices) {
            increaseSize()
        }
        this.vertexCount = vertexCount
        GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, id)
        GL20.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, floats)
    }

    private fun increaseSize() {
        maxVertices *= 2
        val floats = emptyFloatBuffer(maxVertices*attributePointer.coordinateSize)
        GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, id)
        GL20.glBufferData(GL20.GL_ARRAY_BUFFER, floats, GL20.GL_STATIC_DRAW)
    }

}