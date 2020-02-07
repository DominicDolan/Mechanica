package shader

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import shader.script.ShaderScript
import kotlin.system.exitProcess

class ShaderLoader(vertex: ShaderScript, fragment: ShaderScript) {

    val id: Int
    private val vertexShaderID: Int
    private val fragmentShaderID: Int

    init {
        vertexShaderID = loadShader(vertex.script, GL20.GL_VERTEX_SHADER)
        fragmentShaderID = loadShader(fragment.script, GL20.GL_FRAGMENT_SHADER)
        id = GL20.glCreateProgram()
        GL20.glAttachShader(id, vertexShaderID)
        GL20.glAttachShader(id, fragmentShaderID)

//        bindAttribute(0, "position")
//        bindAttribute(1, "textureCoords")

        GL20.glLinkProgram(id)
        GL20.glValidateProgram(id)

        vertex.loadProgram(id)
        fragment.loadProgram(id)
    }

    private fun bindAttribute(attribute: Int, variableName: String) {
        GL20.glBindAttribLocation(id, attribute, variableName)
    }

    private fun loadShader(script: String, type: Int): Int {
        val shaderID = GL20.glCreateShader(type)
        GL20.glShaderSource(shaderID, script)
        GL20.glCompileShader(shaderID)
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.err.println("Could not compile shader!")
            System.err.println(GL20.glGetShaderInfoLog(shaderID, 2000))
            System.err.println(script)
            exitProcess(-1)
        }
        return shaderID
    }


    fun cleanUp() {
        GL20.glDetachShader(id, vertexShaderID)
        GL20.glDetachShader(id, fragmentShaderID)
        GL20.glDeleteShader(vertexShaderID)
        GL20.glDeleteShader(fragmentShaderID)
        GL20.glDeleteProgram(id)
    }
}