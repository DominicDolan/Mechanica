package com.mechanica.engine.context.loader

import com.mechanica.engine.context.GLInitializer
import com.mechanica.engine.shader.qualifiers.AttributeQualifier
import com.mechanica.engine.shader.qualifiers.Qualifier
import com.mechanica.engine.shader.script.Shader
import com.mechanica.engine.shader.script.ShaderScript
import com.mechanica.engine.vertices.ElementArrayType

interface GLLoader {

    val constants: GLConstants
    val bufferLoader: BufferLoader
    val fontLoader: FontLoader
    val graphicsLoader: GraphicsLoader

    fun createAttributeLoader(qualifier: AttributeQualifier): AttributeLoader
    fun createUniformLoader(qualifier: Qualifier): UniformLoader
    fun createElementArray(): ElementArrayType
    fun defaultShader(
            vertex: ShaderScript,
            fragment: ShaderScript,
            tessellation: ShaderScript?,
            geometry: ShaderScript?): Shader

    companion object : GLLoader by GLInitializer.loader
}