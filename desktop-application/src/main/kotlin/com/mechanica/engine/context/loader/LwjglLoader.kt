package com.mechanica.engine.context.loader

import com.mechanica.engine.display.GLFWWindow
import com.mechanica.engine.shader.qualifiers.AttributeQualifier
import com.mechanica.engine.shader.qualifiers.Qualifier
import com.mechanica.engine.shader.script.Shader
import com.mechanica.engine.shader.script.ShaderLoader
import com.mechanica.engine.shader.script.ShaderScript
import com.mechanica.engine.shader.vbo.LwjglElementArrayType
import org.lwjgl.opengl.GL20

class LwjglLoader : GLLoader {
    override val constants = LwjglConstants()
    override val bufferLoader = LwjglBufferLoader()
    override val fontLoader = LwjglFontLoader()
    override val graphicsLoader = LwjglGraphicsLoader()
    override val audioLoader = LwjglAudioLoader()
    override val inputLoader = LwjglInputLoader()

    override fun createAttributeLoader(qualifier: AttributeQualifier) = LwjglAttributeLoader(qualifier)

    override fun createUniformLoader(qualifier: Qualifier) = LwjglUniformLoader(qualifier)

    override fun createElementArray() = LwjglElementArrayType()

    override fun defaultShader(vertex: ShaderScript, fragment: ShaderScript, tessellation: ShaderScript?, geometry: ShaderScript?): Shader {
        return object : Shader() {
            override val vertex = vertex
            override val fragment = fragment
            override val tessellation = tessellation
            override val geometry = geometry

            private val loader: ShaderLoader by lazy { ShaderLoader(vertex, fragment, tessellation, geometry) }

            override val id: Int get() = loader.id

            override fun loadProgram(id: Int) {
                GL20.glUseProgram(id)
            }

            override fun loadUniformLocation(name: String) = GL20.glGetUniformLocation(id, name)
        }
    }
}