package com.mechanica.engine.gl.script

import com.mechanica.engine.gl.Bindable
import com.mechanica.engine.gl.vars.uniforms.UniformVar
import org.lwjgl.opengl.GL20

abstract class ShaderScript : ShaderDeclarations("autoVal") {
    val script: String
        get() = generateScript()

    private var programId: Int = 0

    abstract val main: String

    private val inputs: Array<Bindable?> = Array(20) { null }

    private fun generateScript(): String {
        val sb = StringBuilder(header)
        sb.appendln(declarations)
        sb.appendln(main.trimIndent())

        return sb.toString()
    }

    internal fun loadProgram(programId: Int) {
        this.programId = programId
        for (v in iterator) {
            if (v is UniformVar<*>) {
                v.location = GL20.glGetUniformLocation(programId, v.locationName)
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