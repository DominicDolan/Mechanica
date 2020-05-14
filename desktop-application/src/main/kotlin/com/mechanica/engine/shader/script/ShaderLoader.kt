package com.mechanica.engine.shader.script

import com.mechanica.engine.util.scriptWithLineNumbers
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL32.*
import kotlin.system.exitProcess

class ShaderLoader(
        vertex: ShaderScript,
        fragment: ShaderScript,
        tessellation: ShaderScript?,
        geometry: ShaderScript?): ShaderDeclarations() {

    val id: Int

    private val vertexShaderID: Int
    private val fragmentShaderID: Int

    private val tessellationShaderID: Int?
    private val geometryShaderID: Int?

    init {
        vertexShaderID = loadShader(vertex.script, GL20.GL_VERTEX_SHADER)

        geometryShaderID = if (geometry != null) {
            loadShader(geometry.script, GL_GEOMETRY_SHADER)
        } else null

        fragmentShaderID = loadShader(fragment.script, GL20.GL_FRAGMENT_SHADER)

        tessellationShaderID = null
        if (tessellation != null) {
            TODO()// implement tessellation
        }

        id = GL20.glCreateProgram()
        GL20.glAttachShader(id, vertexShaderID)

        if (geometryShaderID != null) {
            GL20.glAttachShader(id, geometryShaderID)
        }

        GL20.glAttachShader(id, fragmentShaderID)

        fun checkProgram(programId: Int, statusToCheck: Int, errorMessage: (String) -> String) {
            if (GL20.glGetProgrami(programId, statusToCheck) == GL11.GL_FALSE) {
                val log = GL20.glGetProgramInfoLog(programId, GL20.GL_INFO_LOG_LENGTH)
                val script = getShaderScriptFromLog(log, vertex, fragment, tessellation, geometry)

                System.err.println(errorMessage(script))
                System.err.println(log)
                cleanUp()
                exitProcess(-1)
            }
        }


        GL20.glLinkProgram(id)
        checkProgram(id, GL20.GL_LINK_STATUS) { "Shader linking failed\n${scriptWithLineNumbers(it)}" }
        GL20.glValidateProgram(id)
        checkProgram(id, GL20.GL_VALIDATE_STATUS) { "Shader validating failed" }

        vertex.loadProgram(id)
        geometry?.loadProgram(id)
        fragment.loadProgram(id)
    }

    private fun loadShader(script: String, type: Int): Int {
        val shaderID = GL20.glCreateShader(type)
        GL20.glShaderSource(shaderID, script)
        GL20.glCompileShader(shaderID)
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.err.println("Shader compilation failed")
            System.err.println(scriptWithLineNumbers(script))
            System.err.println(GL20.glGetShaderInfoLog(shaderID, GL20.GL_INFO_LOG_LENGTH))
            cleanUp()
            exitProcess(-1)
        }
        return shaderID
    }

    private fun getShaderScriptFromLog(log: String,
                                       vertex: ShaderScript,
                                       fragment: ShaderScript,
                                       tessellation: ShaderScript?,
                                       geometry: ShaderScript?): String {
        return when (getShaderTypeFromLog(log)) {
            0 -> vertex.script
            1 -> fragment.script
            2 -> tessellation?.script ?: ""
            3 -> geometry?.script ?: ""
            else -> ""
        }
    }

    private fun getShaderTypeFromLog(log: String): Int {
        val lowerCase = log.toLowerCase()
        return when {
            lowerCase.contains("vertex") -> 0
            lowerCase.contains("fragment") -> 1
            lowerCase.contains("tessellation") -> 2
            lowerCase.contains("geometry") -> 3
            else -> -1
        }
    }

    fun cleanUp() {
        GL20.glDetachShader(id, vertexShaderID)
        GL20.glDetachShader(id, fragmentShaderID)
        GL20.glDeleteShader(vertexShaderID)
        GL20.glDeleteShader(fragmentShaderID)
        GL20.glDeleteProgram(id)
    }
}