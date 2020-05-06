package com.mechanica.engine.gl.vars.uniforms

import com.mechanica.engine.gl.qualifiers.Qualifier
import com.mechanica.engine.gl.vars.uniforms.UniformVar
import com.mechanica.engine.unit.vector.DynamicVector

abstract class UniformVector2f(
        x: Number, y: Number,
        override val name: String,
        override val qualifier: Qualifier
) : UniformVar<DynamicVector>(), DynamicVector by DynamicVector.create() {
    override val value: DynamicVector by lazy { this }
    override val type: String = "vec2"
}