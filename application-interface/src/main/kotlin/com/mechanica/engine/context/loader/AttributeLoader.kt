package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.vars.attributes.AttributeVariable
import com.mechanica.engine.vertices.AttributeArray
import com.mechanica.engine.vertices.VertexBuffer


interface AttributeLoader {
    fun createAttributeArray(arraySize: Int, variable: AttributeVariable): AttributeArray
    fun createAttributeIntArray(arraySize: Int, variable: AttributeVariable): VertexBuffer<IntArray>
    fun createAttributeShortArray(arraySize: Int, variable: AttributeVariable): VertexBuffer<ShortArray>
}