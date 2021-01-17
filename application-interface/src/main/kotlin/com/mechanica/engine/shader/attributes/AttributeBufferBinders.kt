package com.mechanica.engine.shader.attributes

import com.mechanica.engine.context.loader.MechanicaLoader
import com.mechanica.engine.models.Bindable
import com.mechanica.engine.shader.vars.GlslLocation
import com.mechanica.engine.shader.vars.ShaderType
import com.mechanica.engine.utils.GLPrimitive


interface AttributeBufferBinder : Bindable, GlslLocation {
    val primitiveType: GLPrimitive
}

abstract class FloatAttributeBinder : AttributeBufferBinder {
    override val primitiveType = MechanicaLoader.glPrimitives.glFloat

    companion object {
        private val loader = MechanicaLoader.shaderLoader.attributeLoader

        fun create(location: GlslLocation, type: ShaderType<*>): FloatAttributeBinder {
            return loader.createFloatAttributeBinder(location, type)
        }

        fun create(variable: AttributeVar<*>): FloatAttributeBinder {
            return create(variable, variable.type)
        }
    }
}

abstract class IntAttributeBinder : AttributeBufferBinder {
    override val primitiveType = MechanicaLoader.glPrimitives.glInt
}
