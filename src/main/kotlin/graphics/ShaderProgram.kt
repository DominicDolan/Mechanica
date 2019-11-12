package graphics

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import resources.InternalResource

import kotlin.system.exitProcess


@Suppress("LeakingThis") // This class should be updated or replaced in such a way that this warning no longer occurs
abstract class ShaderProgram(vertexFile: String, fragmentFile: String) {

    private val matrixBuffer = BufferUtils.createFloatBuffer(16)
    private val programID: Int
    private val vertexShaderID: Int
    private val fragmentShaderID: Int

    init {
        vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER)
        fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER)
        programID = GL20.glCreateProgram()
        GL20.glAttachShader(programID, vertexShaderID)
        GL20.glAttachShader(programID, fragmentShaderID)
        bindAttributes()
        GL20.glLinkProgram(programID)
        GL20.glValidateProgram(programID)
        getAllUniformLocations()
    }

    protected abstract fun getAllUniformLocations()

    protected fun getUniformLocation(uniformName: String): Int {
        return GL20.glGetUniformLocation(programID, uniformName)
    }

    protected abstract fun bindAttributes()


    fun setUniform4fv(uniformLocation: Int, vec4: FloatArray) {
        GL20.glUniform4f(uniformLocation, vec4[0], vec4[1], vec4[2], vec4[3])
    }

    fun setUniform2fv(uniformLocation: Int, vec2: FloatArray) {
        GL20.glUniform2f(uniformLocation, vec2[0], vec2[1])
    }

    protected fun bindAttribute(attribute: Int, variableName: String) {
        GL20.glBindAttribLocation(programID, attribute, variableName)
    }

    fun loadFloat(location: Int, value: Float) {
        GL20.glUniform1f(location, value)
    }

    protected fun loadVector(location: Int, vector: Vector3f) {
        GL20.glUniform3f(location, vector.x, vector.y, vector.z)
    }

    protected fun loadVector(location: Int, x: Float, y: Float, z: Float) {
        GL20.glUniform3f(location, x, y, z)
    }

    protected fun loadVector(location: Int, x: Float, y: Float, z: Float, w: Float) {
        GL20.glUniform4f(location, x, y, z, w)
    }

    protected fun loadVector(location: Int, x: Float, y: Float) {
        GL20.glUniform2f(location, x, y)
    }

    protected fun loadVector(location: Int, vector: Vector2f) {
        GL20.glUniform2f(location, vector.x, vector.y)
    }

    protected fun loadBoolean(location: Int, value: Boolean) {
        GL20.glUniform1f(location, (if (value) 1 else 0).toFloat())
    }

    protected fun loadMatrix(location: Int, matrix: Matrix4f) {
        matrixBuffer.clear()
        matrix.get(matrixBuffer)
//        matrix.store(matrixBuffer)
        GL20.glUniformMatrix4fv(location, false, matrixBuffer)

    }


    fun start() {
        GL20.glUseProgram(programID)
    }

    fun stop() {
        GL20.glUseProgram(0)
    }

    fun cleanUp() {
        stop()
        GL20.glDetachShader(programID, vertexShaderID)
        GL20.glDetachShader(programID, fragmentShaderID)
        GL20.glDeleteShader(vertexShaderID)
        GL20.glDeleteShader(fragmentShaderID)
        GL20.glDeleteProgram(programID)
    }


    private fun loadShader(file: String, type: Int): Int {
        val shaderSource = InternalResource("shaders/$file").contents

        val shaderID = GL20.glCreateShader(type)
        GL20.glShaderSource(shaderID, shaderSource)
        GL20.glCompileShader(shaderID)
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            println(GL20.glGetShaderInfoLog(shaderID, 500))
            System.err.println("Could not compile shader!")
            exitProcess(-1)
        }
        return shaderID
    }

}
