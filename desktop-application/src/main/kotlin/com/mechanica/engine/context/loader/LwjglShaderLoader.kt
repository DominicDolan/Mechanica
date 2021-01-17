package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.LwjglShaderCreator
import com.mechanica.engine.shader.script.PlatformScriptValues
import com.mechanica.engine.shader.script.ShaderScript

class LwjglShaderLoader : ShaderLoader {

    override val attributeLoader: AttributeLoader = LwjglAttributeLoader()
    override val uniformLoader = LwjglUniformLoader()

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

