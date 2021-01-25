package com.mechanica.engine.shader

import com.mechanica.engine.shaders.context.ShaderCreator
import com.mechanica.engine.shaders.script.ShaderDeclarations
import com.mechanica.engine.shaders.script.ShaderScript
import com.mechanica.engine.util.scriptWithLineNumbers
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER
import kotlin.system.exitProcess

class LwjglShaderCreator(
        vertex: ShaderScript,
        fragment: ShaderScript,
        tessellation: ShaderScript?,
        geometry: ShaderScript?)
    : ShaderDeclarations(), ShaderCreator {

    override val id: Int = GL20.glCreateProgram()

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

        GL20.glAttachShader(id, vertexShaderID)

        if (geometryShaderID != null) {
            GL20.glAttachShader(id, geometryShaderID)
        }

        GL20.glAttachShader(id, fragmentShaderID)

        fun checkProgram(programId: Int, statusToCheck: Int, errorMessage: (String) -> String) {
            if (GL20.glGetProgrami(programId, statusToCheck) == GL11.GL_FALSE) {
                val log = GL20.glGetProgramInfoLog(programId, GL20.GL_INFO_LOG_LENGTH)
                val script = getShaderScriptFromLog(log, vertex, fragment, tessellation, geometry)

                cleanUp()
                throw IllegalStateException(errorMessage(script) + "\n" + log)
            }
        }


        GL20.glLinkProgram(id)
        checkProgram(id, GL20.GL_LINK_STATUS) { "Shader linking failed\n${scriptWithLineNumbers(it)}" }
        GL20.glValidateProgram(id)
        checkProgram(id, GL20.GL_VALIDATE_STATUS) { "Shader validating failed" }

    }

    override fun useShader() {
        GL20.glUseProgram(id)
    }

    override fun loadUniformLocation(name: String): Int {
        return GL20.glGetUniformLocation(id, name)
    }

    override fun loadAttributeLocation(name: String): Int {
        return GL20.glGetAttribLocation(id, name)
    }

    private fun loadShader(script: String, type: Int): Int {
        val shaderID = GL20.glCreateShader(type)
        GL20.glShaderSource(shaderID, script)
        GL20.glCompileShader(shaderID)
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.err.println("Shader compilation failed")
            System.err.println(scriptWithLineNumbers(script))
            System.err.println(GL20.glGetShaderInfoLog(shaderID, GL20.GL_INFO_LOG_LENGTH))

            GL20.glDetachShader(shaderID, type)
            GL20.glDeleteShader(shaderID)
            
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