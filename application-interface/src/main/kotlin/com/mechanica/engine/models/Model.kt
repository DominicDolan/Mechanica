package com.mechanica.engine.models

import com.mechanica.engine.shader.attributes.Attribute
import com.mechanica.engine.shader.attributes.AttributeArray
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.utils.createInvertedUnitSquareVectors
import com.mechanica.engine.utils.createUnitSquareVectors
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
                if (vbo is AttributeArray && vbo.vertexCount > max) {
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
            val positionArray = AttributeArray.create(createUnitSquareVectors(), Attribute.position)

            val tc = AttributeArray.create(createInvertedUnitSquareVectors(), Attribute.textureCoords)
            return Model(positionArray, tc)
        }

        fun createFromFloatArray(array: FloatArray): Model {
            val position = AttributeArray.create(array, Attribute.position)
            return Model(position)
        }

        fun createFromVecArray(array: Array<Vector>): Model {
            val position = AttributeArray.create(array, Attribute.position)
            return Model(position)
        }
    }
}