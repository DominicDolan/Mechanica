package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.qualifiers.AttributeQualifier
import com.mechanica.engine.shader.qualifiers.Qualifier
import com.mechanica.engine.shader.vbo.LwjglElementArrayType

class LwjglLoader : GLLoader {
    override val constants = LwjglConstants()
    override val bufferLoader = LwjglBufferLoader()
    override val fontLoader = LwjglFontLoader()
    override val graphicsLoader = LwjglGraphicsLoader()

    override fun createAttributeLoader(qualifier: AttributeQualifier) = LwjglAttributeLoader(qualifier)

    override fun createUniformLoader(qualifier: Qualifier) = LwjglUniformLoader(qualifier)

    override fun createElementArray() = LwjglElementArrayType()
}