package com.mechanica.engine.shader.script

import com.mechanica.engine.context.loader.GLLoader
import com.mechanica.engine.models.Bindable
import com.mechanica.engine.models.Model

abstract class Shader {
    abstract val id: Int

    abstract val vertex: ShaderScript
    abstract val fragment: ShaderScript
    open val tessellation: ShaderScript? = null
    open val geometry: ShaderScript? = null

    private var locationsFound = false

    protected fun load() {
        loadProgram(id)
        loadUniforms()
    }

    protected abstract fun loadProgram(id: Int)

    private fun loadUniforms() {
        if (!locationsFound) findLocations()

        vertex.loadVariables()
        fragment.loadVariables()
        tessellation?.loadVariables()
        geometry?.loadVariables()
    }

    abstract fun loadUniformLocation(name: String): Int

    open fun render(inputs: Array<Bindable>, draw: () -> Unit) {
        load()

        inputs.forEach { it.bind() }

        draw()
    }

    open fun render(model: Model) {
        load()

        model.bind()

        model.draw()
    }

    private fun findLocations() {
        vertex.loadUniformLocations(this)
        geometry?.loadUniformLocations(this)
        fragment.loadUniformLocations(this)
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
            return GLLoader.defaultShader(vertex, fragment, tessellation, geometry)
        }
    }
}