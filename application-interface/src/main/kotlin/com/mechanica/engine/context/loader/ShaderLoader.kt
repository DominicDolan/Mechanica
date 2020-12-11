package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.attributes.AttributeVars
import com.mechanica.engine.shader.attributes.FloatAttributeBinder
import com.mechanica.engine.shader.qualifiers.AttributeQualifier
import com.mechanica.engine.shader.script.PlatformScriptValues
import com.mechanica.engine.shader.script.ShaderScript
import com.mechanica.engine.shader.uniforms.UniformVars
import com.mechanica.engine.shader.vars.GlslLocation
import com.mechanica.engine.shader.vars.ShaderType
import com.mechanica.engine.utils.FloatBufferLoader
import com.mechanica.engine.utils.IntBufferLoader
import com.mechanica.engine.utils.ShortBufferLoader

interface ShaderLoader {
    val platformScriptValues: PlatformScriptValues

    val attributeLoader: AttributeLoader
    val uniformLoader: UniformLoader

    fun createShaderFunctions(vertex: ShaderScript,
                              fragment: ShaderScript,
                              tessellation: ShaderScript?,
                              geometry: ShaderScript?): ShaderFunctions

    fun createFloatAttributeBinder(location: GlslLocation, type: ShaderType<*>): FloatAttributeBinder

}

interface UniformLoader {
    val variables: UniformVars
    fun createLocationLoader(locationName: String): GlslLocation
}

interface AttributeLoader {
    fun createLocationLoader(locationName: String): GlslLocation
    fun variables(qualifier: AttributeQualifier) : AttributeVars

    fun disableAttributeArray(value: Int)
}

interface ShaderFunctions {
    val id: Int
    val glslHeader: String
    fun useShader()
    fun loadUniformLocation(name: String): Int
    fun loadAttributeLocation(name: String): Int
}

interface GLBufferLoaders {
    fun floats(floats: FloatArray): FloatBufferLoader
    fun shorts(shorts: ShortArray): ShortBufferLoader
    fun ints(ints: IntArray): IntBufferLoader
}