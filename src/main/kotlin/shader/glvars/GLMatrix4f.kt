package shader.glvars

import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer

class GLMatrix4f(
        var matrix: Matrix4f,
        override val name: String,
        override val qualifier: String
) : GLVar<FloatBuffer>() {
    override var value: FloatBuffer = BufferUtils.createFloatBuffer(16)
    override val type: String = "mat4"

    init {
        set(matrix)
    }

    fun set(matrix: Matrix4f) {
        value.clear()
        matrix.get(value)
        this.matrix = matrix
    }

}