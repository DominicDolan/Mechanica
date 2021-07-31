package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.LwjglShaderCreator
import com.mechanica.engine.shaders.context.AttributeFactory
import com.mechanica.engine.shaders.context.ShaderCreator
import com.mechanica.engine.shaders.context.ShaderFactory
import com.mechanica.engine.shaders.draw.DrawFactories
import com.mechanica.engine.shaders.script.PlatformScriptValues
import com.mechanica.engine.shaders.script.ShaderScript

class LwjglShaderFactory : ShaderFactory {

    override val attributeFactory: AttributeFactory = LwjglAttributeFactory()
    override val uniformFactory = LwjglUniformFactory()

    override val bufferFactories = LwjglBufferFactories()
    override val imageFactory = LwjglImageFactory()
    override val glPrimitives = LwjglPrimitiveFactory()
    override val drawFactories: DrawFactories = LwjglDrawFactories()
    override val fontFactory = LwjglFontFactory()

    override val platformScriptValues = object : PlatformScriptValues {
        val version = "430"
        override val header: String
            get() = "#version $version core\n\n"
    }

    override fun createShaderCreator(
            vertex: ShaderScript,
            fragment: ShaderScript,
            tessellation: ShaderScript?,
            geometry: ShaderScript?): ShaderCreator = LwjglShaderCreator(vertex, fragment, tessellation, geometry)

}

