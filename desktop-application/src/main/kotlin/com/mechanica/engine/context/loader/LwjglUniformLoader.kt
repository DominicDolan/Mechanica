package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.*
import com.mechanica.engine.shader.script.Shader
import com.mechanica.engine.shader.vars.GlslLocation
import org.joml.Matrix4f
import org.lwjgl.opengl.GL20

class LwjglUniformLoader: UniformLoader {
    override fun createLocationLoader(locationName: String) = object : GlslLocation {
        override var location: Int = 0
            private set

        override fun setLocation(shader: Shader) {
            location = GL20.glGetUniformLocation(shader.id, locationName)
        }
    }
    override fun createUniformFloat(name: String, initial: Float) = LwjglFloat(initial, name)
    override fun createUniformVec2(name: String, x: Number, y: Number) = LwjglVector2f(x, y, name)
    override fun createUniformVec3(name: String, x: Number, y: Number, z: Number) = LwjglVector3f(x, y, z, name)
    override fun createUniformVec4(name: String, x: Number, y: Number, z: Number, w: Number) = LwjglVector4f(x, y, z, w, name)
    override fun createUniformMat4(name: String, initial: Matrix4f) = LwjglMatrix4f(initial, name)
}