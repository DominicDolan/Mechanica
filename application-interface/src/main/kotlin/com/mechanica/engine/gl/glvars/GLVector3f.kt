package com.mechanica.engine.gl.glvars

import com.mechanica.engine.gl.context.GL
import org.joml.Vector3f

class GLVector3f(
        x: Number, y: Number, z: Number,
        override val name: String,
        override val qualifier: String
) : GLVar<Vector3f>() {
    override var value: Vector3f = Vector3f()
    override val type: String = "vec3"

    fun set(x: Number, y: Number, z: Number) {
        value.x = x.toFloat()
        value.y = x.toFloat()
        value.z = x.toFloat()
    }

    override fun loadUniform() {
        GL.glUniform3f(location, value.x, value.y, value.z)
    }
}