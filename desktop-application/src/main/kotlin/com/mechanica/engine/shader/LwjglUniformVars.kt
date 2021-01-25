package com.mechanica.engine.shader

import com.mechanica.engine.shaders.uniforms.*
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20

class LwjglFloat(
        value: Float,
        name: String
) : UniformFloat(value, name) {
    override fun loadUniform() {
        GL20.glUniform1f(location, value)
    }
}

class LwjglVector2f(
        x: Number, y: Number,
        name: String
) : UniformVector2f(x, y, name) {

    override fun loadUniform() {
        GL20.glUniform2f(location, x.toFloat(), y.toFloat())
    }
}

class LwjglVector3f(
        x: Number, y: Number, z: Number,
        name: String
) : UniformVector3f(x, y, z, name) {
    override fun loadUniform() {
        GL20.glUniform3f(location, value.x, value.y, value.z)
    }
}

class LwjglVector4f (
        x: Number, y: Number, z: Number, w: Number,
        name: String
) : UniformVector4f(x, y, z, w, name) {
    override fun loadUniform() {
        GL20.glUniform4f(location, value.x, value.y, value.z, value.w)
    }
}

class LwjglMatrix4f(
        matrix: Matrix4f,
        name: String
): UniformMatrix4f(matrix, name) {
    private val buffer = BufferUtils.createFloatBuffer(16)

    override fun loadUniform() {
        matrix.get(buffer)
        GL20.glUniformMatrix4fv(location, false, buffer)
    }
}