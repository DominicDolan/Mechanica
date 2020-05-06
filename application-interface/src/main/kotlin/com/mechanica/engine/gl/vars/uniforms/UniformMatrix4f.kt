package com.mechanica.engine.gl.vars.uniforms

import com.mechanica.engine.gl.qualifiers.Qualifier
import org.joml.Matrix4f

abstract class UniformMatrix4f(
        var matrix: Matrix4f,
        override val name: String,
        override val qualifier: Qualifier
) : UniformVar<Matrix4f>() {
    override val value: Matrix4f = Matrix4f().identity()
    override val type: String = "mat4"

    init {
        set(matrix)
    }

    fun set(matrix: Matrix4f) {
        this.matrix.set(matrix)
    }
}