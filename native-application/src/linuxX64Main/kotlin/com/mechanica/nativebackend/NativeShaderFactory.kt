package com.mechanica.nativebackend

import com.mechanica.engine.shaders.context.ShaderFactory
import com.mechanica.engine.shaders.script.PlatformScriptValues
import com.mechanica.engine.shaders.script.ShaderScript
import com.mechanica.engine.shaders.text.FontFactory

/** Shader backend. A font factory is supplied by the host until Native fonts are implemented. */
class NativeShaderFactory(override val fontFactory: FontFactory) : ShaderFactory {
    override val platformScriptValues = object : PlatformScriptValues {
        override val header: String = "#version 430 core\n\n"
    }
    override val attributeFactory = NativeAttributeFactory()
    override val uniformFactory = NativeUniformFactory()
    override val bufferFactories = NativeBufferFactories()
    override val imageFactory = NativeImageFactory()
    override val glPrimitives = NativePrimitiveFactory()
    override val drawFactories = NativeDrawFactories()
    override fun createShaderCreator(vertex: ShaderScript, fragment: ShaderScript, tessellation: ShaderScript?, geometry: ShaderScript?) =
        NativeShaderCreator(vertex, fragment, tessellation, geometry)
}
