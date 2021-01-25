package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.LwjglShaderCreator
import com.mechanica.engine.shaders.context.AttributeLoader
import com.mechanica.engine.shaders.context.ShaderCreator
import com.mechanica.engine.shaders.context.ShaderLoader
import com.mechanica.engine.shaders.draw.DrawLoader
import com.mechanica.engine.shaders.script.PlatformScriptValues
import com.mechanica.engine.shaders.script.ShaderScript

class LwjglShaderLoader : ShaderLoader {

    override val attributeLoader: AttributeLoader = LwjglAttributeLoader()
    override val uniformLoader = LwjglUniformLoader()

    override val bufferLoader = LwjglBufferLoader()
    override val imageLoader = LwjglImageLoader()
    override val glPrimitives = LwjglPrimitiveLoader()
    override val drawLoader: DrawLoader = LwjglDrawLoader()
    override val fontLoader = LwjglFontLoader()

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

