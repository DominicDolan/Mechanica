package com.mechanica.engine.gl.context.loader

import com.mechanica.engine.gl.qualifiers.Qualifier
import com.mechanica.engine.gl.vars.uniforms.*
import org.joml.Matrix4f

interface UniformLoader {
    val qualifier: Qualifier
    fun createUniformFloat(name: String, initial: Float): UniformFloat
    fun createUniformVec2(name: String, x: Number, y: Number): UniformVector2f
    fun createUniformVec3(name: String, x: Number, y: Number, z: Number): UniformVector3f
    fun createUniformVec4(name: String, x: Number, y: Number, z: Number, w: Number): UniformVector4f
    fun createUniformMat4(name: String, initial: Matrix4f): UniformMatrix4f
}