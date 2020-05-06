package com.mechanica.engine.gl.vbo

import com.mechanica.engine.color.Color
import com.mechanica.engine.gl.Bindable
import com.mechanica.engine.gl.context.loader.GLLoader
import com.mechanica.engine.gl.qualifiers.Qualifier
import com.mechanica.engine.gl.vars.ShaderVariable
import com.mechanica.engine.unit.vector.Vector
import org.joml.Vector3f
import org.joml.Vector4f


interface VertexBufferType<T> {
    val coordinateSize: Int
    fun createBuffer(array: T) : VertexBuffer<T>
}

interface ElementArrayType: VertexBufferType<ShortArray> {
    override val coordinateSize: Int
        get() = 1
    override fun createBuffer(array: ShortArray): ElementArrayBuffer

    companion object : ElementArrayType by GLLoader.createElementArray()
}

interface AttributeType<T>: VertexBufferType<T>, Bindable {
    val location: Int
}

interface FloatAttributeType: AttributeType<FloatArray> {
    fun createBuffer(array: Array<Vector>): VertexFloatArray
    fun createBuffer(array: Array<Vector3f>): VertexFloatArray
    fun createBuffer(array: Array<Vector4f>): VertexFloatArray
    fun createBuffer(array: Array<Color>): VertexFloatArray
}

class ShaderAttribute<T> (
        attributeType: AttributeType<T>,
        override val name: String,
        override val qualifier: Qualifier,
        override val type: String) : ShaderVariable, AttributeType<T> by attributeType {

    override fun toString() = name
}

class ShaderFloatAttributeType (
        attributeType: FloatAttributeType,
        override val name: String,
        override val qualifier: Qualifier,
        override val type: String) : ShaderVariable, FloatAttributeType by attributeType {

    override fun toString() = name
}