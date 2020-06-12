package com.mechanica.engine.shader.uniforms.vars

import com.mechanica.engine.color.Color
import com.mechanica.engine.color.LightweightColor
import com.mechanica.engine.shader.qualifiers.Qualifier
import com.mechanica.engine.shader.vars.ShaderType
import org.joml.Vector4f
import kotlin.reflect.KProperty


abstract class UniformVector4f (
        x: Number, y: Number, z: Number, w: Number,
        override val name: String,
        override val qualifier: Qualifier
) : UniformVar<Vector4f>() {
    override var value: Vector4f = Vector4f()
    override val type = ShaderType.vec4()

    init { set(x, y, z, w) }

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

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Vector4f) {
        this.value.set(value)
    }
}