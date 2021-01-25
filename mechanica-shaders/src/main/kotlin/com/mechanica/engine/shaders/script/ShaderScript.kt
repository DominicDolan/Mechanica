package com.mechanica.engine.shaders.script

import com.mechanica.engine.shaders.attributes.AttributeVar
import com.mechanica.engine.shaders.models.Bindable
import com.mechanica.engine.shaders.uniforms.UniformVar

abstract class ShaderScript : ShaderDeclarations() {
    val script: String
        get() = generateScript()

    abstract val main: String

    private val inputs: Array<Bindable?> = Array(20) { null }

    private fun generateScript(): String {
        val sb = StringBuilder(header)
        sb.appendLine(declarations)
        sb.appendLine(main.trimIndent())

        return sb.toString()
    }

    fun loadVariableLocations(shader: Shader) {
        for (v in iterator) {
            v.setLocation(shader)
        }
    }

    internal fun loadVariables() {
        for (v in iterator) {
            if (v is UniformVar<*>) {
                v.loadUniform()
            }
            if (v is AttributeVar<*>) {
                v.loadAttribute()
            }
        }
    }

}