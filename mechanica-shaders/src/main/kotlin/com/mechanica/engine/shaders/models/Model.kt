package com.mechanica.engine.shaders.models

import com.mechanica.engine.shaders.attributes.Attribute
import com.mechanica.engine.shaders.attributes.AttributeArray
import com.mechanica.engine.shaders.utils.ElementIndexArray
import com.mechanica.engine.shaders.utils.createInvertedUnitSquareVectors
import com.mechanica.engine.shaders.utils.createUnitSquareVectors
import com.mechanica.engine.unit.vector.Vector

open class Model(vararg inputs: Bindable) : Iterable<Bindable> {
    protected val inputs: Array<Bindable> = arrayOf(*inputs)

    private val maxVertices: Int
        get() {
            var max = 0
            for (vbo in inputs) {
                if (vbo is ElementIndexArray && vbo.vertexCount > max) {
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
            if (input is ElementIndexArray) {
                hasElementArrayBuffer = true
            }
        }

        this.hasIndexArray = hasElementArrayBuffer
    }

    fun bind() {
        for (vbo in inputs) {
            vbo.bind()
        }
    }

    override fun iterator() = inputs.iterator()

    companion object {
        fun createUnitSquare(): Model {
            val positionArray = AttributeArray.createPositionArray(createUnitSquareVectors())

            val tc = AttributeArray.createTextureArray(createInvertedUnitSquareVectors())
            return Model(positionArray, tc)
        }

        fun createFromFloatArray(array: FloatArray): Model {
            val position = AttributeArray.create(array, Attribute.positionLocation)
            return Model(position)
        }

        fun createFromVecArray(array: Array<Vector>): Model {
            val position = AttributeArray.create(array, Attribute.positionLocation)
            return Model(position)
        }
    }
}