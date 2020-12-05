package com.mechanica.engine.vertices

import com.mechanica.engine.models.Bindable
import com.mechanica.engine.utils.createIndicesArrayForQuads

interface VertexBuffer<T> : Bindable {
    val id: Int
    val vertexCount: Int
    val T.valueCount: Int

    fun safeBind() {
        if (vertexCount > 0) {
            bind()
        } else {
            unbind()
        }
    }

    fun set(array: T, from: Int = 0, length: Int = array.valueCount)
}

interface IndexArray : VertexBuffer<ShortArray> {
    companion object {
        fun create(vararg indices: Short) = ElementArrayType.createBuffer(indices)

        /**
         *  Uses [createIndicesArrayForQuads] function to generate
         *  indices for vertices that are organised as 2D quads.
         */
        fun createIndicesForQuads(quadCount: Int) = ElementArrayType.createBuffer(createIndicesArrayForQuads(quadCount))
        fun create(size: Int) = ElementArrayType.createBuffer(ShortArray(size))
    }
}