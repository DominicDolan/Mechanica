package com.mechanica.engine.vertices

import com.mechanica.engine.color.Color
import com.mechanica.engine.models.Bindable
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.utils.createIndicesArrayForQuads
import org.joml.Vector3f
import org.joml.Vector4f


interface VertexBuffer<T> : Bindable {
    val id: Int
    val vertexCount: Int
    val T.valueCount: Int

    fun set(array: T, from: Int = 0, length: Int = array.valueCount)
}

interface AttributeArray : VertexBuffer<FloatArray> {
    fun set(array: Array<out Vector>, from: Int = 0, length: Int = array.size)
    fun set(array: Array<Vector3f>, from: Int = 0, length: Int = array.size)
    fun set(array: Array<Vector4f>, from: Int = 0, length: Int = array.size)
    fun set(array: Array<out Color>, from: Int = 0, length: Int = array.size)
    fun disable()
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