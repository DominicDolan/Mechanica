package com.mechanica.engine.gl.glvars

import com.mechanica.engine.color.Color
import com.mechanica.engine.color.LightweightColor
import com.mechanica.engine.gl.context.GL
import org.joml.Vector4f


class GLVector4f (
        x: Number, y: Number, z: Number, w: Number,
        override val name: String,
        override val qualifier: String
) : GLVar<Vector4f>() {
    override var value: Vector4f = Vector4f()
    override val type: String = "vec4"

    fun set(color: Color) {
        value.x = color.r.toFloat()
        value.y = color.g.toFloat()
        value.z = color.b.toFloat()
        value.w = color.a.toFloat()
    }

    fun set(color: LightweightColor) {
        value.x = color.r.toFloat()
        value.y = color.g.toFloat()
        value.z = color.b.toFloat()
        value.w = color.a.toFloat()
    }

    fun set(x: Number, y: Number, z: Number, w: Number) {
        value.x = x.toFloat()
        value.y = y.toFloat()
        value.z = z.toFloat()
        value.w = w.toFloat()
    }

    override fun loadUniform() {
        GL.glUniform4f(location, value.x, value.y, value.z, value.w)
    }
}