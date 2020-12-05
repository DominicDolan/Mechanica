package com.mechanica.engine.shader.attributes

import com.mechanica.engine.models.Bindable
import com.mechanica.engine.shader.qualifiers.AttributeQualifier
import com.mechanica.engine.shader.script.Shader
import com.mechanica.engine.shader.vars.GlslLocation
import com.mechanica.engine.shader.vars.ShaderType
import com.mechanica.engine.shader.vars.ShaderVar
import com.mechanica.engine.shader.vars.ShaderVars
import com.mechanica.engine.unit.vector.DynamicVector
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f

interface AttributeVars : ShaderVars<AttributeQualifier> {

    override fun float(name: String?): AttributeFloat

    override fun vec2(name: String?): AttributeVector2

    override fun vec3(name: String?): AttributeVector3

    override fun vec4(name: String?): AttributeVector4

    override fun mat4(name: String?): AttributeMatrix4
}

abstract class AttributeVar<T> : ShaderVar<T, AttributeQualifier>, Bindable {
    private val binder: FloatAttributeBinder by lazy { FloatAttributeBinder.create(this) }
    private val glslLocation: GlslLocation by lazy { GlslLocation.create(this) }

    override val qualifier = Attribute.qualifier

    override val location: Int
        get() = glslLocation.location

    abstract fun loadAttribute()

    override fun bind() {
        binder.bind()
    }

    override fun setLocation(shader: Shader) {
        glslLocation.setLocation(shader)
    }

    override fun toString() = name

}

abstract class AttributeFloat(
        override val name: String
) : AttributeVar<Float>() {
    override val type = ShaderType.float()
}

abstract class AttributeVector2(
        override val name: String
) : AttributeVar<DynamicVector>() {
    override val type = ShaderType.vec2()
}

abstract class AttributeVector3(
        override val name: String
) : AttributeVar<Vector3f>() {
    override val type = ShaderType.vec3()
}

abstract class AttributeVector4(
        override val name: String
) : AttributeVar<Vector4f>() {
    override val type = ShaderType.vec4()
}

abstract class AttributeMatrix4(
        override val name: String
) : AttributeVar<Matrix4f>() {
    override val type = ShaderType.mat4()
}