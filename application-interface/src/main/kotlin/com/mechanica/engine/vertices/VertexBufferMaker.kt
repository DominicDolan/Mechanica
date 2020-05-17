package com.mechanica.engine.vertices

import com.mechanica.engine.color.Color
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.utils.createQuadVecArray
import com.mechanica.engine.utils.createUnitSquareVecArray
import org.joml.Vector3f
import org.joml.Vector4f

interface VertexBufferMaker<T> {
    fun createBuffer(array: T) : VertexBuffer<T>
}

interface FloatBufferMaker : VertexBufferMaker<FloatArray> {

    fun createBuffer(array: Array<out Vector>): AttributeArray
    fun createBuffer(array: Array<Vector3f>): AttributeArray
    fun createBuffer(array: Array<Vector4f>): AttributeArray
    fun createBuffer(array: Array<out Color>): AttributeArray

    fun createUnitQuad() = createBuffer(createQuadVecArray(0f, 1f, 1f, 0f))
    fun createInvertedUnitQuad() = createBuffer(createQuadVecArray(0f, 0f, 1f, 1f))
}
