package com.mechanica.engine.gl.shader

import com.mechanica.engine.gl.models.Model
import com.mechanica.engine.gl.script.ShaderScript
import com.mechanica.engine.gl.Bindable
import com.mechanica.engine.matrix.Matrices
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
        vertex.loadVariables()
        fragment.loadVariables()
        tessellation?.loadVariables()
        geometry?.loadVariables()
    }

    abstract fun loadMatrices(transformation: Matrix4f, projection: Matrix4f?, view: Matrix4f?)

    fun render(model: Model, transformation: Matrix4f, projection: Matrix4f? = null, view: Matrix4f? = null) {

        loadMatrices(transformation, projection, view)

        GL20.glUseProgram(id)

        load()

        model.bind()
        model.draw(model)
    }

    fun render(inputs: Array<Bindable>, draw: () -> Unit) {

        GL20.glUseProgram(id)

        inputs.forEach { it.bind() }
        load()

        draw()
    }

    protected open class MatrixLoader(script: ShaderScript) {
        val matrixType = script.uniform.float("matrixType")

        val projection = script.uniform.mat4("projection")
        val transformation = script.uniform.mat4("transformation")
        val view = script.uniform.mat4("view")

    }

    companion object {
        var defaultMatrices = object : Matrices {
            override val projection = Matrix4f().identity()
            override val view = Matrix4f().identity()
            override val uiView = Matrix4f().identity()
        }

        operator fun invoke(
                vertex: ShaderScript,
                fragment: ShaderScript,
                tessellation: ShaderScript? = null,
                geometry: ShaderScript? = null): Shader {
            return ShaderImpl(vertex, fragment, tessellation, geometry)
        }
    }
}