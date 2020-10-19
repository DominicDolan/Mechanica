package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.vars.attributes.AttributeDefinition
import com.mechanica.engine.shader.vbo.LwjglAttributeBuffer
import com.mechanica.engine.vertices.AttributeArray
import com.mechanica.engine.vertices.VertexBuffer

class LwjglAttributeLoader() : AttributeLoader {

    override fun createAttributeArray(arraySize: Int, definition: AttributeDefinition): AttributeArray {
        return LwjglAttributeBuffer(arraySize, definition)
    }

    override fun createAttributeIntArray(arraySize: Int, definition: AttributeDefinition): VertexBuffer<IntArray> {
        TODO("not implemented")
    }

    override fun createAttributeShortArray(arraySize: Int, definition: AttributeDefinition): VertexBuffer<ShortArray> {
        TODO("not implemented")
    }
}