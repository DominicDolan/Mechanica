package com.mechanica.engine.gl.vars.uniforms

import com.mechanica.engine.color.Color
import com.mechanica.engine.color.LightweightColor
import com.mechanica.engine.gl.qualifiers.Qualifier
import com.mechanica.engine.gl.vars.uniforms.UniformVar
import org.joml.Vector4f


abstract class UniformVector4f (
        x: Number, y: Number, z: Number, w: Number,
        override val name: String,
        override val qualifier: Qualifier
) : UniformVar<Vector4f>() {
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
}