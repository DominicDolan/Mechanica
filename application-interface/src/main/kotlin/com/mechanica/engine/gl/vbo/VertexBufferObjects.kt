package com.mechanica.engine.gl.vbo

import com.mechanica.engine.color.Color
import com.mechanica.engine.gl.Bindable
import com.mechanica.engine.gl.context.loader.GLLoader
import com.mechanica.engine.unit.vector.Vector
import org.joml.Vector3f
import org.joml.Vector4f
import java.nio.IntBuffer
import java.nio.ShortBuffer


interface VertexBuffer<T> : Bindable {
    val id: Int
    val type: VertexBufferType<T>
    val T.valueCount: Int

    fun set(array: T, from: Int = 0, length: Int = array.valueCount)
}

interface VertexFloatArray : VertexBuffer<FloatArray> {
    fun set(array: Array<Vector>, from: Int = 0, length: Int = array.size)
    fun set(array: Array<Vector3f>, from: Int = 0, length: Int = array.size)
    fun set(array: Array<Vector4f>, from: Int = 0, length: Int = array.size)
    fun set(array: Array<Color>, from: Int = 0, length: Int = array.size)
}

interface VertexIntArray : VertexBuffer<IntArray>
interface VertexIntBuffer : VertexBuffer<IntBuffer>

interface VertexShortArray : VertexBuffer<ShortArray>
interface VertexShortBuffer : VertexBuffer<ShortBuffer>

interface ElementArrayBuffer : VertexBuffer<ShortArray> {
    override val type: ElementArrayType

    companion object {
        fun create(vararg indices: Short) = ElementArrayType.createBuffer(indices)
    }
}