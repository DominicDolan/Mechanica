package shader

import org.joml.Matrix4f
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import shader.script.Declarations
import shader.script.ShaderScript

class Shader(private val vertex: ShaderScript, private val fragment: ShaderScript) {
    private var _loader: ShaderLoader? = null
    private val loader: ShaderLoader
        get() {
            val l = this._loader
            val loader = l ?: ShaderLoader(vertex, fragment)
            this._loader = loader
            return loader
        }
    val id: Int
        get() = loader.id

    private var activeVBOs = 0

    fun load() {
        GL20.glUseProgram(loader.id)
        loadUniforms()
    }

    fun loadUniforms() {
        vertex.loadUniforms()
        fragment.loadUniforms()
    }

    private fun prepareVertexArrays(vaoID: Int, vboCount: Int) {
        GL30.glBindVertexArray(vaoID)
        activeVBOs = vboCount
        for (i in 0 until vboCount) {
            GL20.glEnableVertexAttribArray(i)
        }
    }

    private fun disableVertexArrays() {
        for (i in 0 until activeVBOs) {
            GL20.glDisableVertexAttribArray(i)
        }
        activeVBOs = 0

        GL30.glBindVertexArray(0)
    }

    companion object {
        fun loadTransformationMatrix(matrix: Matrix4f) {
            Declarations.transformation.set(matrix)
        }

        fun loadProjectionMatrix(matrix: Matrix4f) {
            Declarations.projection.set(matrix)
        }

        fun loadViewMatrix(matrix: Matrix4f) {
            Declarations.view.set(matrix)
        }
    }
}