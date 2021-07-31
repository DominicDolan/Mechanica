package com.mechanica.engine.shaders.context

import com.mechanica.engine.shaders.attributes.AttributeVars
import com.mechanica.engine.shaders.attributes.FloatAttributeBinder
import com.mechanica.engine.shaders.buffers.BufferFactories
import com.mechanica.engine.shaders.buffers.FloatBufferObject
import com.mechanica.engine.shaders.buffers.IntBufferObject
import com.mechanica.engine.shaders.buffers.ShortBufferObject
import com.mechanica.engine.shaders.draw.DrawFactories
import com.mechanica.engine.shaders.glPrimitives.GLPrimitiveFactory
import com.mechanica.engine.shaders.qualifiers.AttributeQualifier
import com.mechanica.engine.shaders.script.PlatformScriptValues
import com.mechanica.engine.shaders.script.ShaderScript
import com.mechanica.engine.shaders.text.FontFactory
import com.mechanica.engine.shaders.uniforms.UniformVars
import com.mechanica.engine.shaders.vars.GlslLocation
import com.mechanica.engine.shaders.vars.ShaderType

interface ShaderFactory {
    val platformScriptValues: PlatformScriptValues

    val attributeFactory: AttributeFactory
    val uniformFactory: UniformFactory

    val bufferFactories: BufferFactories

    val imageFactory: ImageFactory

    val glPrimitives: GLPrimitiveFactory

    val drawFactories: DrawFactories

    val fontFactory: FontFactory

    fun createShaderCreator(vertex: ShaderScript,
                            fragment: ShaderScript,
                            tessellation: ShaderScript?,
                            geometry: ShaderScript?): ShaderCreator

    companion object : ShaderFactory by MechanicaShadersInitializer.factory
}

interface UniformFactory {
    val variables: UniformVars
    fun createLocationLoader(locationName: String): GlslLocation
}

interface AttributeFactory {
    fun createLocationLoader(locationName: String): GlslLocation
    fun variables(qualifier: AttributeQualifier) : AttributeVars

    fun createFloatAttributeBinder(location: GlslLocation, type: ShaderType<*>): FloatAttributeBinder
}

interface ShaderCreator {
    val id: Int
    fun useShader()
    fun loadUniformLocation(name: String): Int
    fun loadAttributeLocation(name: String): Int
}

interface GLBufferFactory {
    fun floats(floats: FloatArray): FloatBufferObject
    fun shorts(shorts: ShortArray): ShortBufferObject
    fun ints(ints: IntArray): IntBufferObject
}