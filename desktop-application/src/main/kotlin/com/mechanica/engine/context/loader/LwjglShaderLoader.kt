package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.attributes.FloatAttributeBinder
import com.mechanica.engine.shader.attributes.FloatBufferLoader
import com.mechanica.engine.shader.script.PlatformScriptValues
import com.mechanica.engine.shader.script.Shader
import com.mechanica.engine.shader.script.ShaderCreator
import com.mechanica.engine.shader.script.ShaderScript
import com.mechanica.engine.shader.vars.GlslLocation
import com.mechanica.engine.shader.vars.ShaderType
import com.mechanica.engine.shader.vbo.LwjglElementArrayType
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL15.glGenBuffers
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL40

class LwjglShaderLoader : ShaderLoader {

    override val attributeLoader: AttributeLoader = LwjglAttributeLoader()
    override val uniformLoader = LwjglUniformLoader()

    override val platformScriptValues = object : PlatformScriptValues {
        var version = "430"
        override val header: String
            get() = "#version $version core\n\n"
    }


    override fun createElementArray() = LwjglElementArrayType()

    override fun createShaderFunctions(
            vertex: ShaderScript,
            fragment: ShaderScript,
            tessellation: ShaderScript?,
            geometry: ShaderScript?): ShaderFunctions = ShaderCreator(vertex, fragment, tessellation, geometry)

    override fun createFloatBufferLoader(floats: FloatArray) = object : FloatBufferLoader() {
        override val floats: FloatArray = floats
        override val id: Int = glGenBuffers()
        val bufferTarget: Int = GL40.GL_ARRAY_BUFFER

        override fun initiateBuffer() {
            GL40.glBufferData(bufferTarget, byteCapacity, GL40.GL_STATIC_DRAW)
        }

        override fun storeSubData(offset: Long) {
            GL15.glBufferSubData(bufferTarget, offset, floats)
        }

        override fun bind() {
            GL40.glBindBuffer(bufferTarget, id)
        }

    }

    override fun createFloatAttributeBinder(location: GlslLocation, type: ShaderType<*>) = LwjglFloatAttributeBinder(location, type)

    class LwjglFloatAttributeBinder(private val locatable: GlslLocation, private val type: ShaderType<*>) : FloatAttributeBinder() {
        override val location: Int
            get() = locatable.location

        override fun bind() {
            GL40.glVertexAttribPointer(location, type.coordinateSize, primitiveType.id, false, 0, 0)
            GL40.glEnableVertexAttribArray(location)
        }

        override fun unbind() {
            GL20.glDisableVertexAttribArray(location)
        }

        override fun setLocation(shader: Shader) {
            locatable.setLocation(shader)
        }
    }

}