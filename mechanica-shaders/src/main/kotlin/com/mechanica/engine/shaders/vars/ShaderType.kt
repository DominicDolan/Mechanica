package com.mechanica.engine.shaders.vars

import com.cave.library.matrix.mat4.Matrix4
import com.cave.library.vector.vec2.VariableVector2
import com.cave.library.vector.vec3.Vector3
import com.cave.library.vector.vec4.Vector4

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

class Vec2ShaderType : ShaderType<VariableVector2> {
    override val type: String = "vec2"
    override val coordinateSize: Int = 2

    override fun toString() = type
}

class Vec3ShaderType : ShaderType<Vector3> {
    override val type: String = "vec3"
    override val coordinateSize: Int = 3

    override fun toString() = type
}

class Vec4ShaderType : ShaderType<Vector4> {
    override val type: String = "vec4"
    override val coordinateSize: Int = 4

    override fun toString() = type
}

class Mat4ShaderType : ShaderType<Matrix4> {
    override val type: String = "mat4"
    override val coordinateSize: Int = 16

    override fun toString() = type
}