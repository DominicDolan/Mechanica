package com.mechanica.engine.gl.context.loader.attributes

import com.mechanica.engine.gl.vbo.AttributeType
import com.mechanica.engine.gl.vbo.FloatAttributeType

interface AttributeLoader {
    fun createFloatAttribute(coordinateSize: Int): FloatAttributeType
    fun createIntAttribute(coordinateSize: Int): AttributeType<IntArray>
    fun createShortAttribute(coordinateSize: Int): AttributeType<ShortArray>
}