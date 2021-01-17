package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.attributes.AttributeVars
import com.mechanica.engine.shader.attributes.FloatAttributeBinder
import com.mechanica.engine.shader.qualifiers.AttributeQualifier
import com.mechanica.engine.shader.script.PlatformScriptValues
import com.mechanica.engine.shader.script.ShaderScript
import com.mechanica.engine.shader.uniforms.UniformVars
import com.mechanica.engine.shader.vars.GlslLocation
import com.mechanica.engine.shader.vars.ShaderType
import com.mechanica.engine.utils.FloatBufferObject
import com.mechanica.engine.utils.IntBufferObject
import com.mechanica.engine.utils.ShortBufferObject

interface ShaderLoader {
    val platformScriptValues: PlatformScriptValues

    val attributeLoader: AttributeLoader
    val uniformLoader: UniformLoader

    fun createShaderCreator(vertex: ShaderScript,
                            fragment: ShaderScript,
                            tessellation: ShaderScript?,
                            geometry: ShaderScript?): ShaderCreator

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