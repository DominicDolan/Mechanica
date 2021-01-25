package com.mechanica.engine.shaders.context

import com.mechanica.engine.shaders.attributes.AttributeVars
import com.mechanica.engine.shaders.attributes.FloatAttributeBinder
import com.mechanica.engine.shaders.buffers.BufferLoader
import com.mechanica.engine.shaders.buffers.FloatBufferObject
import com.mechanica.engine.shaders.buffers.IntBufferObject
import com.mechanica.engine.shaders.buffers.ShortBufferObject
import com.mechanica.engine.shaders.draw.DrawLoader
import com.mechanica.engine.shaders.glPrimitives.GLPrimitiveLoader
import com.mechanica.engine.shaders.qualifiers.AttributeQualifier
import com.mechanica.engine.shaders.script.PlatformScriptValues
import com.mechanica.engine.shaders.script.ShaderScript
import com.mechanica.engine.shaders.text.FontLoader
import com.mechanica.engine.shaders.uniforms.UniformVars
import com.mechanica.engine.shaders.vars.GlslLocation
import com.mechanica.engine.shaders.vars.ShaderType

interface ShaderLoader {
    val platformScriptValues: PlatformScriptValues

    val attributeLoader: AttributeLoader
    val uniformLoader: UniformLoader

    val bufferLoader: BufferLoader

    val imageLoader: ImageLoader

    val glPrimitives: GLPrimitiveLoader

    val drawLoader: DrawLoader

    val fontLoader: FontLoader

    fun createShaderCreator(vertex: ShaderScript,
                            fragment: ShaderScript,
                            tessellation: ShaderScript?,
                            geometry: ShaderScript?): ShaderCreator

    companion object : ShaderLoader by MechanicaShadersInitializer.loader
}

interface UniformLoader {
    val variables: UniformVars
    fun createLocationLoader(locationName: String): GlslLocation
}

interface AttributeLoader {
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