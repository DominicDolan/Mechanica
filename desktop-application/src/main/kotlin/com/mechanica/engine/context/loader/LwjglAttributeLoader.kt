package com.mechanica.engine.context.loader

import com.mechanica.engine.context.loader.AttributeLoader
import com.mechanica.engine.shader.qualifiers.AttributeQualifier
import com.mechanica.engine.shader.vars.attributes.AttributeVariable
import com.mechanica.engine.shader.vbo.*
import com.mechanica.engine.vertices.AttributeArray
import com.mechanica.engine.vertices.VertexBuffer

class LwjglAttributeLoader(private val qualifier: AttributeQualifier) : AttributeLoader {

    override fun createAttributeArray(arraySize: Int, variable: AttributeVariable): AttributeArray {
        return LwjglAttributeBuffer(arraySize, variable)
    }

    override fun createAttributeIntArray(arraySize: Int, variable: AttributeVariable): VertexBuffer<IntArray> {
        TODO("not implemented")
    }

    override fun createAttributeShortArray(arraySize: Int, variable: AttributeVariable): VertexBuffer<ShortArray> {
        TODO("not implemented")
    }
}