package com.mechanica.engine.shaders.attributes

import com.cave.library.matrix.mat4.Matrix4
import com.cave.library.vector.vec2.VariableVector2
import com.cave.library.vector.vec3.Vector3
import com.cave.library.vector.vec4.Vector4
import com.mechanica.engine.shaders.models.Bindable
import com.mechanica.engine.shaders.qualifiers.AttributeQualifier
import com.mechanica.engine.shaders.script.Shader
import com.mechanica.engine.shaders.vars.GlslLocation
import com.mechanica.engine.shaders.vars.ShaderType
import com.mechanica.engine.shaders.vars.ShaderVar
import com.mechanica.engine.shaders.vars.ShaderVars

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
) : AttributeVar<VariableVector2>() {
    override val type = ShaderType.vec2()
}

abstract class AttributeVector3(
        override val name: String
) : AttributeVar<Vector3>() {
    override val type = ShaderType.vec3()
}

abstract class AttributeVector4(
        override val name: String
) : AttributeVar<Vector4>() {
    override val type = ShaderType.vec4()
}

abstract class AttributeMatrix4(
        override val name: String
) : AttributeVar<Matrix4>() {
    override val type = ShaderType.mat4()
}