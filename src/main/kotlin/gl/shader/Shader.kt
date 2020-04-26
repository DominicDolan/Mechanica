package gl.shader

import drawer.shader.DrawerScript
import gl.models.Model
import gl.script.ShaderScript
import org.joml.Matrix4f
import org.lwjgl.opengl.GL20

abstract class Shader {
    abstract val vertex: ShaderScript
    abstract val fragment: ShaderScript
    abstract val tessellation: ShaderScript?
    abstract val geometry: ShaderScript?

    abstract val id: Int

    private fun load() {
        GL20.glUseProgram(id)
        loadUniforms()
    }

    private fun loadUniforms() {
        vertex.loadUniforms()
        fragment.loadUniforms()
        tessellation?.loadUniforms()
        geometry?.loadUniforms()
    }

    abstract fun loadMatrices(transformation: Matrix4f, projection: Matrix4f?, view: Matrix4f?)

    fun render(model: Model, transformation: Matrix4f, projection: Matrix4f? = null, view: Matrix4f? = null) {

        loadMatrices(transformation, projection, view)

        GL20.glUseProgram(id)

        load()

        model.bind()
        model.draw(model)
    }

    protected open class MatrixLoader(script: DrawerScript) {
        val matrixType = script.qualifier("uniform").float("matrixType")

        val projection = script.qualifier("uniform").mat4("projection")
        val transformation = script.qualifier("uniform").mat4("transformation")
        val view = script.qualifier("uniform").mat4("view")

    }

    companion object {
        operator fun invoke(
                vertex: ShaderScript,
                fragment: ShaderScript,
                tessellation: ShaderScript? = null,
                geometry: ShaderScript? = null): Shader {
            return ShaderImpl(vertex, fragment, tessellation, geometry)
        }
    }
}