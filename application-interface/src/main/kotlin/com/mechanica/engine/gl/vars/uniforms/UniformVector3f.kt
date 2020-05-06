package com.mechanica.engine.gl.vars.uniforms

import com.mechanica.engine.gl.qualifiers.Qualifier
import com.mechanica.engine.gl.vars.uniforms.UniformVar
import org.joml.Vector3f

abstract class UniformVector3f(
        x: Number, y: Number, z: Number,
        override val name: String,
        override val qualifier: Qualifier
) : UniformVar<Vector3f>() {
    override var value: Vector3f = Vector3f()
    override val type: String = "vec3"

    fun set(x: Number, y: Number, z: Number) {
        value.x = x.toFloat()
        value.y = x.toFloat()
        value.z = x.toFloat()
    }
}