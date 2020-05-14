package com.mechanica.engine.shader.script

import com.mechanica.engine.models.Bindable
import com.mechanica.engine.models.Model
import org.lwjgl.opengl.GL20

abstract class Shader {
    abstract val vertex: ShaderScript
    abstract val fragment: ShaderScript
    abstract val tessellation: ShaderScript?
    abstract val geometry: ShaderScript?

    abstract val id: Int

    protected fun load() {
        GL20.glUseProgram(id)
        loadUniforms()
    }

    private fun loadUniforms() {
        vertex.loadVariables()
        fragment.loadVariables()
        tessellation?.loadVariables()
        geometry?.loadVariables()
    }

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