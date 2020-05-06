package com.mechanica.engine.gl.vars.uniforms

import com.mechanica.engine.gl.qualifiers.Qualifier
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.glUniformMatrix4fv

class LwjglMatrix4f(
        matrix: Matrix4f,
        name: String,
        qualifier: Qualifier
): UniformMatrix4f(matrix, name, qualifier) {
    private val buffer = BufferUtils.createFloatBuffer(16)

    override fun loadUniform() {
        matrix.get(buffer)
        glUniformMatrix4fv(location, false, buffer)
    }
}