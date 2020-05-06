package com.mechanica.engine.gl.vars.uniforms

import com.mechanica.engine.gl.qualifiers.Qualifier
import com.mechanica.engine.gl.vars.ShaderVariable

abstract class UniformVar<T> : ShaderVariable {
    abstract val value: T
    abstract override val name: String
    abstract override val qualifier: Qualifier
    abstract override val type: String
    abstract fun loadUniform()

    var location = 0

    private val regex = Regex("[^\\w\\d]")
    val locationName: String
        get() {
            val index = regex.find(name)?.range?.first
            return if (index != null) {
                name.substring(0 until index)
            } else name
        }

    override fun toString() = name
}