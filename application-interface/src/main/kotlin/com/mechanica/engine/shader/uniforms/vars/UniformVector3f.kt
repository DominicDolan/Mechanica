package com.mechanica.engine.shader.uniforms.vars

import com.mechanica.engine.shader.qualifiers.Qualifier
import com.mechanica.engine.shader.vars.ShaderType
import org.joml.Vector3f
import kotlin.reflect.KProperty

abstract class UniformVector3f(
        x: Number, y: Number, z: Number,
        override val name: String,
        override val qualifier: Qualifier
) : UniformVar<Vector3f>() {
    override var value: Vector3f = Vector3f()
    override val type = ShaderType.vec3()

    init { set(x, y, z) }

    fun set(x: Number, y: Number, z: Number) {
        value.x = x.toFloat()
        value.y = y.toFloat()
        value.z = z.toFloat()
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Vector3f) {
        this.value.set(value)
    }
}