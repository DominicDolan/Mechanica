package com.mechanica.engine.models

import com.mechanica.engine.context.loader.GLLoader
import com.mechanica.engine.vertices.IndexArray
import com.mechanica.engine.vertices.VertexBuffer

open class Model(vararg inputs: Bindable,
                 draw: ((Model) -> Unit)? = null) : Iterable<Bindable> {
    protected val inputs: Array<Bindable> = arrayOf(*inputs)

    private val maxVertices: Int
        get() {
            var max = 0
            for (vbo in inputs) {
                if (vbo is VertexBuffer<*> && vbo.vertexCount > max) {
                    max = vbo.vertexCount
                }
            }
            return max
        }
    var vertexCount = maxVertices

    private val draw: ((Model) -> Unit) by lazy { draw ?: defaultDraw(this) }

    fun bind() {
        for (vbo in inputs) {
            vbo.bind()
        }
    }

    fun draw() {
        this.draw(this)
    }

    override fun iterator() = inputs.iterator()

    companion object {
        fun defaultDraw(model: Model)  = if (model.inputs.any { it is IndexArray }) {
                GLLoader.graphicsLoader::drawElements
            } else {
                GLLoader.graphicsLoader::drawArrays
            }
    }
}