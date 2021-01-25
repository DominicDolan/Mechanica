package com.mechanica.engine.shaders.vars

import com.mechanica.engine.unit.vector.DynamicVector
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f

interface ShaderType<T> {
    val type: String
    val coordinateSize: Int
    override fun toString(): String
    companion object {
        fun float() = FloatShaderType()
        fun vec2() = Vec2ShaderType()
        fun vec3() = Vec3ShaderType()
        fun vec4() = Vec4ShaderType()
        fun mat4() = Mat4ShaderType()
    }
}

class FloatShaderType : ShaderType<Float> {
    override val type: String = "float"
    override val coordinateSize: Int = 1

    override fun toString() = type
}

class Vec2ShaderType : ShaderType<DynamicVector> {
    override val type: String = "vec2"
    override val coordinateSize: Int = 2

    override fun toString() = type
}

class Vec3ShaderType : ShaderType<Vector3f> {
    override val type: String = "vec3"
    override val coordinateSize: Int = 3

    override fun toString() = type
}

class Vec4ShaderType : ShaderType<Vector4f> {
    override val type: String = "vec4"
    override val coordinateSize: Int = 4

    override fun toString() = type
}

class Mat4ShaderType : ShaderType<Matrix4f> {
    override val type: String = "mat4"
    override val coordinateSize: Int = 16

    override fun toString() = type
}