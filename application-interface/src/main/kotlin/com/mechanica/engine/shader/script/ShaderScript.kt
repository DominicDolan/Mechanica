package com.mechanica.engine.shader.script

import com.mechanica.engine.models.Bindable
import com.mechanica.engine.shader.vars.uniforms.vars.UniformVar

abstract class ShaderScript : ShaderDeclarations("autoVal") {
    val script: String
        get() = generateScript()

    abstract val main: String

    private val inputs: Array<Bindable?> = Array(20) { null }

    private fun generateScript(): String {
        val sb = StringBuilder(header)
        sb.appendln(declarations)
        sb.appendln(main.trimIndent())

        return sb.toString()
    }

    fun loadUniformLocations(shader: Shader) {
        for (v in iterator) {
            if (v is UniformVar<*>) {
                v.location = shader.loadUniformLocation(v.locationName)
            }
        }
    }

    internal fun loadVariables() {
        for (v in iterator) {
            if (v is UniformVar<*>) {
                v.loadUniform()
            }
        }
    }

}