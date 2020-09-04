package com.mechanica.engine.models

import com.mechanica.engine.shader.qualifiers.Attribute
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.vertices.IndexArray
import com.mechanica.engine.vertices.VertexBuffer

open class Model(vararg inputs: Bindable) : Iterable<Bindable> {
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

    val hasIndexArray: Boolean

    init {
        var hasElementArrayBuffer = false
        for (input in inputs) {
            if (input is IndexArray) {
                hasElementArrayBuffer = true
            }
        }

        this.hasIndexArray = hasElementArrayBuffer
    }

    fun bind() {
        for (vbo in inputs) {
            if (vbo is VertexBuffer<*>) {
                vbo.safeBind()
            } else {
                vbo.bind()
            }
        }
    }

    override fun iterator() = inputs.iterator()

    companion object {

        fun createUnitSquare(): Model {
            val position = Attribute.location(0).vec3().createUnitQuad()
            val tc = Attribute.location(1).vec2().createInvertedUnitQuad()
            return Model(position, tc)
        }

        fun createFromFloatArray(array: FloatArray): Model {
            val position = Attribute.location(0).vec3().createBuffer(array)
            return Model(position)
        }

        fun createFromVecArray(array: Array<Vector>): Model {
            val position = Attribute.location(0).vec3().createBuffer(array)
            return Model(position)
        }
    }
}