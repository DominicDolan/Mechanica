package gl.vbo

import org.lwjgl.opengl.GL20

class IndexVBO internal constructor(override val id: Int, override val vertexCount: Int): VBO {
    override fun bind() {
        GL20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, id)
    }
}