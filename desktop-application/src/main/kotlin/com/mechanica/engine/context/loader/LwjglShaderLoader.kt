package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.attributes.FloatAttributeBinder
import com.mechanica.engine.shader.script.PlatformScriptValues
import com.mechanica.engine.shader.script.Shader
import com.mechanica.engine.shader.script.ShaderCreator
import com.mechanica.engine.shader.script.ShaderScript
import com.mechanica.engine.shader.vars.GlslLocation
import com.mechanica.engine.shader.vars.ShaderType
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

    override fun createShaderFunctions(
            vertex: ShaderScript,
            fragment: ShaderScript,
            tessellation: ShaderScript?,
            geometry: ShaderScript?): ShaderFunctions = ShaderCreator(vertex, fragment, tessellation, geometry)

    override fun createFloatAttributeBinder(location: GlslLocation, type: ShaderType<*>) = LwjglFloatAttributeBinder(location, type)

}

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

