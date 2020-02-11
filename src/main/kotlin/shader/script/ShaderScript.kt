package shader.script

import display.Game
import org.lwjgl.opengl.GL20
import shader.glvars.GLMatrix4f
import java.nio.FloatBuffer

abstract class ShaderScript : Declarations("autoVal") {
    val script: String by lazy { generateScript() }

    private var programId: Int = 0

    abstract val main: String

    private fun generateScript(): String {
        val sb = java.lang.StringBuilder(header)
        sb.appendln(globalDeclarations)
        sb.appendln(declarations)
        sb.appendln(main.trimIndent())
        globalMethodDeclarations(sb)

        return sb.toString()
    }

    internal fun loadProgram(programId: Int) {
        this.programId = programId
        for (v in iterator) {
            if (v.qualifier == "uniform") {
                v.location = GL20.glGetUniformLocation(programId, v.name)
            }
        }
    }


    internal fun loadUniforms() {
        loadGlobalUniforms()
        loadLocalUniforms()
    }

    private fun loadGlobalUniforms() {
        for (v in Declarations.iterator) {
            if (v.qualifier == "uniform") {
                v.location = GL20.glGetUniformLocation(programId, v.name)
                loadUniform(v.location, v.value)
            }
        }
    }

    private fun loadLocalUniforms() {
        for (v in iterator) {
            if (v.qualifier == "uniform") {
                loadUniform(v.location, v.value)
            }
        }
    }

    private fun loadUniform(location: Int, value: Any?) {
        when (value) {
            is Float -> GL20.glUniform1f(location, value)
            is FloatArray -> loadFloatArray(location, value)
            is FloatBuffer -> GL20.glUniformMatrix4fv(location, false, value)
        }
    }

    private fun loadFloatArray(location: Int, array: FloatArray) {
        when (array.size) {
            1 -> GL20.glUniform1f(location, array[0])
            2 -> GL20.glUniform2f(location, array[0], array[1])
            3 -> GL20.glUniform3f(location, array[0], array[1], array[2])
            4 -> GL20.glUniform4f(location, array[0], array[1], array[2], array[3])
        }
    }

}