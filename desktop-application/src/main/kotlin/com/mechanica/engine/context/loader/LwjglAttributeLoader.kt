package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.LwjglAttributeVars
import com.mechanica.engine.shader.attributes.AttributeVars
import com.mechanica.engine.shader.attributes.FloatAttributeBinder
import com.mechanica.engine.shader.qualifiers.AttributeQualifier
import com.mechanica.engine.shader.script.Shader
import com.mechanica.engine.shader.vars.GlslLocation
import com.mechanica.engine.shader.vars.ShaderType
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL40

class LwjglAttributeLoader : AttributeLoader {
    override fun createLocationLoader(locationName: String) = object : GlslLocation {
        override var location: Int = 0
            private set

        override fun setLocation(shader: Shader) {
            this.location = GL20.glGetAttribLocation(shader.id, locationName)
        }
    }

    override fun variables(qualifier: AttributeQualifier): AttributeVars {
        return LwjglAttributeVars(qualifier)
    }

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