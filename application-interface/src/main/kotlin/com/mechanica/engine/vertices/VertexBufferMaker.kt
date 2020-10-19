package com.mechanica.engine.vertices

import com.mechanica.engine.color.Color
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.utils.createQuadVecArray
import org.joml.Vector3f
import org.joml.Vector4f

interface VertexBufferMaker<T> {
    fun createBuffer(array: T) : VertexBuffer<T>
}

interface FloatArrayMaker : VertexBufferMaker<FloatArray> {

    fun createArray(array: Array<out Vector>): AttributeArray
    fun createArray(array: Array<Vector3f>): AttributeArray
    fun createArray(array: Array<Vector4f>): AttributeArray
    fun createArray(array: Array<out Color>): AttributeArray

    fun createUnitQuad() = createArray(createQuadVecArray(0f, 1f, 1f, 0f))
    fun createInvertedUnitQuad() = createArray(createQuadVecArray(0f, 0f, 1f, 1f))
}
