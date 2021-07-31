package com.mechanica.engine.shaders.attributes

import com.mechanica.engine.shaders.context.ShaderFactory
import com.mechanica.engine.shaders.glPrimitives.GLPrimitive
import com.mechanica.engine.shaders.models.Bindable
import com.mechanica.engine.shaders.vars.GlslLocation
import com.mechanica.engine.shaders.vars.ShaderType


interface AttributeBufferBinder : Bindable, GlslLocation {
    val primitiveType: GLPrimitive
}

abstract class FloatAttributeBinder : AttributeBufferBinder {
    override val primitiveType = ShaderFactory.glPrimitives.glFloat

    companion object {
        private val factory = ShaderFactory.attributeFactory

        fun create(location: GlslLocation, type: ShaderType<*>): FloatAttributeBinder {
            return factory.createFloatAttributeBinder(location, type)
        }

        fun create(variable: AttributeVar<*>): FloatAttributeBinder {
            return create(variable, variable.type)
        }
    }
}

abstract class IntAttributeBinder : AttributeBufferBinder {
    override val primitiveType = ShaderFactory.glPrimitives.glInt
}
