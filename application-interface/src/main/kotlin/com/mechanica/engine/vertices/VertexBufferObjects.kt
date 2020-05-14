package com.mechanica.engine.vertices

import com.mechanica.engine.color.Color
import com.mechanica.engine.models.Bindable
import com.mechanica.engine.unit.vector.Vector
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
    fun set(array: Array<Color>, from: Int = 0, length: Int = array.size)
}

interface ElementArrayBuffer : VertexBuffer<ShortArray> {
    companion object {
        fun create(vararg indices: Short) = ElementArrayType.createBuffer(indices)
        fun create(size: Int) = ElementArrayType.createBuffer(ShortArray(size))
    }
}