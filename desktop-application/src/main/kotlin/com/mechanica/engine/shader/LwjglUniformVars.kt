package com.mechanica.engine.shader

import com.cave.library.matrix.mat4.Matrix4
import com.mechanica.engine.shaders.uniforms.*
import org.lwjgl.opengl.GL20

class LwjglFloat(
        value: Float,
        name: String
) : UniformFloat(value, name) {
    override fun loadUniform() {
        GL20.glUniform1f(location, value)
    }
}

class LwjglVector2(
        x: Number, y: Number,
        name: String
) : UniformVector2(x, y, name) {

    override fun loadUniform() {
        GL20.glUniform2f(location, x.toFloat(), y.toFloat())
    }
}

class LwjglVector3(
        x: Number, y: Number, z: Number,
        name: String
) : UniformVector3(x, y, z, name) {
    override fun loadUniform() {
        GL20.glUniform3f(location, value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
    }
}

class LwjglVector4 (
        x: Number, y: Number, z: Number, w: Number,
        name: String
) : UniformVector4(x, y, z, w, name) {
    override fun loadUniform() {
        GL20.glUniform4f(location, value.x.toFloat(), value.y.toFloat(), value.z.toFloat(), value.w.toFloat())
    }
}

class LwjglMatrix4(
        matrix: Matrix4,
        name: String
): UniformMatrix4f(matrix, name) {
    private val array = FloatArray(16)

    override fun loadUniform() {
        matrix.fill(array)
        GL20.glUniformMatrix4fv(location, false, array)
    }
}