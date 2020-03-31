package gl.shader

import gl.models.Model
import gl.script.ShaderScript
import org.joml.Matrix4f
import org.lwjgl.opengl.GL20

interface Shader {
    val vertex: ShaderScript
    val fragment: ShaderScript
    val tessellation: ShaderScript?
    val geometry: ShaderScript?

    val id: Int

    fun load() {
        GL20.glUseProgram(id)
        loadUniforms()
    }

    fun loadUniforms() {
        vertex.loadUniforms()
        fragment.loadUniforms()
        tessellation?.loadUniforms()
        geometry?.loadUniforms()
    }

    fun loadMatrices(transformation: Matrix4f, projection: Matrix4f?, view: Matrix4f?)

    fun render(model: Model, transformation: Matrix4f, projection: Matrix4f? = null, view: Matrix4f? = null) {

        loadMatrices(transformation, projection, view)

        GL20.glUseProgram(id)

        load()

        model.bind()
        model.draw(model)
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