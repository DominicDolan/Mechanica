@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import com.cave.library.matrix.mat4.Matrix4
import com.mechanica.engine.shaders.uniforms.*
import com.mechanica.samples.nativegl.*
import kotlinx.cinterop.*

class NativeFloat(
        value: Float,
        name: String
) : UniformFloat(value, name) {
    override fun loadUniform() {
        glUniform1f(location, value)
    }
}

class NativeVector2(
        x: Number, y: Number,
        name: String
) : UniformVector2(x, y, name) {

    override fun loadUniform() {
        glUniform2f(location, x.toFloat(), y.toFloat())
    }
}

class NativeVector3(
        x: Number, y: Number, z: Number,
        name: String
) : UniformVector3(x, y, z, name) {
    override fun loadUniform() {
        glUniform3f(location, value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
    }
}

class NativeVector4 (
        x: Number, y: Number, z: Number, w: Number,
        name: String
) : UniformVector4(x, y, z, w, name) {
    override fun loadUniform() {
        glUniform4f(location, value.x.toFloat(), value.y.toFloat(), value.z.toFloat(), value.w.toFloat())
    }
}

class NativeMatrix4(
        matrix: Matrix4,
        name: String
): UniformMatrix4f(matrix, name) {
    private val array = FloatArray(16)

    override fun loadUniform() {
        matrix.fill(array)
        array.usePinned { glUniformMatrix4fv(location, 1, GL_FALSE.toUByte(), it.addressOf(0)) }
    }
}