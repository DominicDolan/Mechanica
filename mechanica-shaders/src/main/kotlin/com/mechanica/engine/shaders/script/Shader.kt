package com.mechanica.engine.shaders.script

import com.mechanica.engine.shaders.context.ShaderCreator
import com.mechanica.engine.shaders.context.ShaderFactory
import com.mechanica.engine.shaders.draw.GLDraw
import com.mechanica.engine.shaders.models.Bindable
import com.mechanica.engine.shaders.models.Model
import com.mechanica.engine.util.extensions.fori

abstract class Shader {
    abstract val vertex: ShaderScript
    abstract val fragment: ShaderScript
    open val tessellation: ShaderScript? = null
    open val geometry: ShaderScript? = null

    private var locationsFound = false

    private val gl = GLDraw()

    protected open val loader: ShaderCreator by lazy { ShaderFactory.createShaderCreator(vertex, fragment, tessellation, geometry) }

    val id: Int get() = loader.id

    protected open val defaultDraw: GLDraw.(Model) -> Unit = {
        this.drawModel(it)
    }

    protected fun load() {
        loadProgram()
        loadUniforms()
    }

    private fun loadProgram() {
        loader.useShader()
    }

    private fun loadUniforms() {
        if (!locationsFound) findLocations()

        vertex.loadVariables()
        fragment.loadVariables()
        tessellation?.loadVariables()
        geometry?.loadVariables()
    }

    fun loadUniformLocation(name: String): Int = loader.loadUniformLocation(name)

    open fun render(inputs: Array<Bindable>, draw: GLDraw.() -> Unit) {
        load()

        inputs.fori { it.bind() }

        draw(gl)
    }

    open fun render(input: Bindable, draw: GLDraw.() -> Unit) {
        load()

        input.bind()

        draw(gl)
    }

    open fun render(model: Model, draw: GLDraw.(Model) -> Unit = defaultDraw) {
        load()

        model.bind()

        draw(gl, model)
    }

    private fun findLocations() {
        vertex.loadVariableLocations(this)
        geometry?.loadVariableLocations(this)
        fragment.loadVariableLocations(this)
        locationsFound = true
    }

    companion object {
        operator fun invoke(
                vertex: ShaderScript,
                fragment: ShaderScript,
                tessellation: ShaderScript? = null,
                geometry: ShaderScript? = null): Shader {
            return create(vertex, fragment, tessellation, geometry)
        }

        fun create(
                vertex: ShaderScript,
                fragment: ShaderScript,
                tessellation: ShaderScript? = null,
                geometry: ShaderScript? = null): Shader {
            return object : Shader() {
                override val vertex = vertex
                override val fragment = fragment
                override val tessellation = tessellation
                override val geometry = geometry
            }
        }
    }
}