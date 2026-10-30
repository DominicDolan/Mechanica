@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import com.mechanica.samples.nativegl.*
import com.mechanica.engine.shaders.attributes.AttributeVars
import com.mechanica.engine.shaders.attributes.FloatAttributeBinder
import com.mechanica.engine.shaders.context.AttributeFactory
import com.mechanica.engine.shaders.qualifiers.AttributeQualifier
import com.mechanica.engine.shaders.script.Shader
import com.mechanica.engine.shaders.vars.GlslLocation
import com.mechanica.engine.shaders.vars.ShaderType

class NativeAttributeFactory : AttributeFactory {
    override fun createLocationLoader(locationName: String) = object : GlslLocation {
        override var location: Int = 0
            private set

        override fun setLocation(shader: Shader) {
            this.location = glGetAttribLocation(shader.id.toUInt(), locationName)
        }
    }

    override fun variables(qualifier: AttributeQualifier): AttributeVars {
        return NativeAttributeVars(qualifier)
    }

    override fun createFloatAttributeBinder(location: GlslLocation, type: ShaderType<*>) = NativeFloatAttributeBinder(location, type)
}


class NativeFloatAttributeBinder(private val locatable: GlslLocation, private val type: ShaderType<*>) : FloatAttributeBinder() {
    override val location: Int
        get() = locatable.location

    override fun bind() {
        glVertexAttribPointer(location.toUInt(), type.coordinateSize, primitiveType.id.toUInt(), GL_FALSE.toUByte(), 0, null)
        glEnableVertexAttribArray(location.toUInt())
    }

    override fun unbind() {
        glDisableVertexAttribArray(location.toUInt())
    }

    override fun setLocation(shader: Shader) {
        locatable.setLocation(shader)
    }
}