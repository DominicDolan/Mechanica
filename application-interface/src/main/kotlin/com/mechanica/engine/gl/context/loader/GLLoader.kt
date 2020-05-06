package com.mechanica.engine.gl.context.loader

import com.mechanica.engine.gl.context.GLInitializer
import com.mechanica.engine.gl.context.loader.attributes.AttributeLoader
import com.mechanica.engine.gl.qualifiers.Qualifier
import com.mechanica.engine.gl.vbo.ElementArrayBuffer
import com.mechanica.engine.gl.vbo.ElementArrayType

interface GLLoader {

    val constants: GLConstants

    fun createAttributeLoader(location: Int): AttributeLoader
    fun createUniformLoader(qualifier: Qualifier): UniformLoader
    fun createElementArray(): ElementArrayType

    companion object : GLLoader by GLInitializer.loader
}