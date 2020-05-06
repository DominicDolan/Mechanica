package com.mechanica.engine.gl.loader

import com.mechanica.engine.gl.context.loader.GLLoader
import com.mechanica.engine.gl.qualifiers.Qualifier
import com.mechanica.engine.gl.vbo.ElementArrayBuffer
import com.mechanica.engine.gl.vbo.ElementArrayType
import com.mechanica.engine.gl.vbo.LwjglElementArrayType
import com.mechanica.engine.gl.vbo.VertexBufferType
import org.lwjgl.opengl.GL40

class LwjglLoader : GLLoader {
    override val constants = LwjglConstants()

    override fun createAttributeLoader(location: Int) = LwjglAttributeLoader(location)

    override fun createUniformLoader(qualifier: Qualifier) = LwjglUniformLoader(qualifier)

    override fun createElementArray() = LwjglElementArrayType()
}