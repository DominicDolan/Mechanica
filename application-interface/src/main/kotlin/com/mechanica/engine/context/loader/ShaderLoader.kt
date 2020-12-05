package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.attributes.FloatAttributeBinder
import com.mechanica.engine.shader.attributes.FloatBufferLoader
import com.mechanica.engine.shader.script.PlatformScriptValues
import com.mechanica.engine.shader.script.ShaderScript
import com.mechanica.engine.shader.vars.GlslLocation
import com.mechanica.engine.shader.vars.ShaderType
import com.mechanica.engine.vertices.ElementArrayType

interface ShaderLoader {
    val platformScriptValues: PlatformScriptValues

    val attributeLoader: AttributeLoader
    val uniformLoader: UniformLoader

    fun createShaderFunctions(vertex: ShaderScript,
                              fragment: ShaderScript,
                              tessellation: ShaderScript?,
                              geometry: ShaderScript?): ShaderFunctions

    fun createElementArray(): ElementArrayType
    fun createFloatBufferLoader(floats: FloatArray): FloatBufferLoader
    fun createFloatAttributeBinder(location: GlslLocation, type: ShaderType<*>): FloatAttributeBinder

}