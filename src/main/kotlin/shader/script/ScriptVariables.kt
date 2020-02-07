package shader.script

import shader.glvars.GLVar
import org.lwjgl.opengl.GL20
import java.nio.FloatBuffer

class ScriptVariables {
    private val names = ArrayList<String>()
    private val variables = ArrayList<GLVar<*>>()

    val scriptHead: String
        get() {
            val sb = StringBuilder("#version 400 core\n\n")
            for (v in variables) {
                sb.append(v.declaration)
            }
            return sb.toString()
        }

    fun getNextName(): String {
        val new = "autoVal${names.size}"
        names.add(new)
        return new
    }

    fun addVariable(v: GLVar<*>) {
        variables.add(v)
    }

    fun setLocations(programId: Int) {
        for (v in variables) {
            if (v.firstType == "uniform") {
                v.location = GL20.glGetUniformLocation(programId, v.name)
            }
        }
    }

    fun loadUniforms() {
        for (v in variables) {
            if (v.firstType == "uniform") {
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