package com.mechanica.engine.gl.glvars

import com.mechanica.engine.gl.context.GL
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer

class GLMatrix4f(
        var matrix: Matrix4f,
        override val name: String,
        override val qualifier: String
) : GLVar<Matrix4f>() {
    override val value: Matrix4f = Matrix4f().identity()
    override val type: String = "mat4"
    private val buffer = BufferUtils.createFloatBuffer(16)

    init {
        set(matrix)
    }

    fun set(matrix: Matrix4f) {
        this.matrix.set(matrix)
    }

    override fun loadUniform() {
        matrix.get(buffer)
        GL.glUniformMatrix4fv(location, false, buffer)
    }
}