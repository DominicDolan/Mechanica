package com.mechanica.engine.shader.script

import com.mechanica.engine.context.loader.GLLoader
import com.mechanica.engine.graphics.GLDraw
import com.mechanica.engine.models.Bindable
import com.mechanica.engine.models.Model
import com.mechanica.engine.util.extensions.fori

abstract class Shader {
    abstract val id: Int
    abstract val vertex: ShaderScript
    abstract val fragment: ShaderScript
    open val tessellation: ShaderScript? = null
    open val geometry: ShaderScript? = null

    private var locationsFound = false

    private val glDraw = GLDraw()

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

    open fun render(inputs: Array<Bindable>, draw: GLDraw.() -> Unit) {
        load()

        inputs.fori { it.bind() }

        draw(glDraw)
    }

    open fun render(model: Model) {
        load()

        model.bind()

        glDraw.draw(model)
    }

    protected open fun GLDraw.draw(model: Model) {
        drawModel(model)
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