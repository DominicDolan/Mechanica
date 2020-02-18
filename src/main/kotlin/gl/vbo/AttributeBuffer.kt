package gl.vbo

import gl.AttributePointer
import org.lwjgl.opengl.GL20
import util.extensions.toFloatBuffer
import util.units.Vector
import java.nio.FloatBuffer

class AttributeBuffer internal constructor(
        override val id: Int,
        override val vertexCount: Int,
        private val attributePointer: AttributePointer) : VBO {

    override fun bind() {
        GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, id)
        attributePointer.enable()
    }

}

