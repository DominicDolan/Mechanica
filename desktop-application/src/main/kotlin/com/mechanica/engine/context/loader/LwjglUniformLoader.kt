package com.mechanica.engine.context.loader

import com.mechanica.engine.context.loader.UniformLoader
import com.mechanica.engine.shader.qualifiers.Qualifier
import com.mechanica.engine.shader.uniforms.*
import org.joml.Matrix4f

class LwjglUniformLoader(override val qualifier: Qualifier): UniformLoader {
    override fun createUniformFloat(name: String, initial: Float) = LwjglFloat(initial, name, qualifier)
    override fun createUniformVec2(name: String, x: Number, y: Number) = LwjglVector2f(x, y, name, qualifier)
    override fun createUniformVec3(name: String, x: Number, y: Number, z: Number) = LwjglVector3f(x, y, z, name, qualifier)
    override fun createUniformVec4(name: String, x: Number, y: Number, z: Number, w: Number) = LwjglVector4f(x, y, z, w, name, qualifier)
    override fun createUniformMat4(name: String, initial: Matrix4f) = LwjglMatrix4f(initial, name, qualifier)
}