package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.vars.attributes.AttributeDefinition
import com.mechanica.engine.vertices.AttributeArray
import com.mechanica.engine.vertices.VertexBuffer


interface AttributeLoader {
    fun createAttributeArray(arraySize: Int, definition: AttributeDefinition): AttributeArray
    fun createAttributeIntArray(arraySize: Int, definition: AttributeDefinition): VertexBuffer<IntArray>
    fun createAttributeShortArray(arraySize: Int, definition: AttributeDefinition): VertexBuffer<ShortArray>
}