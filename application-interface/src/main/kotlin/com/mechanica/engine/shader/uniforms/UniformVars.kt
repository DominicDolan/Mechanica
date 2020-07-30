package com.mechanica.engine.shader.uniforms

import com.mechanica.engine.color.Color
import com.mechanica.engine.shader.uniforms.vars.*
import com.mechanica.engine.unit.vector.Vector
import org.joml.Matrix4f
import org.joml.Vector3f

interface UniformVars {

    fun <T> type(type: String, name: String, initialValue: T, load: (T) -> Unit = { }): UniformVar<T>
    fun <T> type(type: String, initialValue: T, load: (T) -> Unit = { }): UniformVar<T>

    fun float(f: Float, name: String? = null): UniformFloat
    fun float(name: String? = null): UniformFloat = float(0f, name)

    fun vec2(x: Number, y: Number, name: String? = null): UniformVector2f
    fun vec2(name: String? = null): UniformVector2f = vec2(0f, 0f, name)
    fun vec2(vector: Vector, name: String? = null): UniformVector2f = vec2(vector.x, vector.y, name)

    fun vec3(x: Number, y: Number, z: Number, name: String? = null): UniformVector3f
    fun vec3(vector: Vector3f, name: String? = null): UniformVector3f = vec3(vector.x, vector.y, vector.z, name)
    fun vec3(name: String? = null): UniformVector3f = vec3(0f, 0f, 0f, name)

    fun vec4(x: Number, y: Number, z: Number, w: Number, name: String? = null): UniformVector4f
    fun vec4(name: String? = null): UniformVector4f = vec4(0f, 0f, 0f, 0f, name)
    fun vec4(color: Color, name: String? = null): UniformVector4f = vec4(color.r, color.g, color.b, color.a, name)

    fun mat4(name: String? = null): UniformMatrix4f

    fun mat4(matrix: Matrix4f, name: String? = null): UniformMatrix4f {
        val v = mat4(name)
        v.set(matrix)
        return v
    }

}