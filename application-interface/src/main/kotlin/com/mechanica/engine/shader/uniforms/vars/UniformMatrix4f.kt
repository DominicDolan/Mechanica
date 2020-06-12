package com.mechanica.engine.shader.uniforms.vars

import com.mechanica.engine.shader.qualifiers.Qualifier
import com.mechanica.engine.shader.vars.ShaderType
import org.joml.Matrix4f
import kotlin.reflect.KProperty

abstract class UniformMatrix4f(
        var matrix: Matrix4f,
        override val name: String,
        override val qualifier: Qualifier
) : UniformVar<Matrix4f>() {
    override val value: Matrix4f = Matrix4f().identity()
    override val type = ShaderType.mat4()

    init {
        set(matrix)
    }

    fun set(matrix: Matrix4f) {
        this.matrix.set(matrix)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Matrix4f) {
        set(value)
    }
}