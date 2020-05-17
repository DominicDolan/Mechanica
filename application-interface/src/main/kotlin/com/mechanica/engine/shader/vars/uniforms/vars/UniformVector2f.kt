package com.mechanica.engine.shader.vars.uniforms.vars

import com.mechanica.engine.shader.qualifiers.Qualifier
import com.mechanica.engine.shader.vars.ShaderType
import com.mechanica.engine.unit.vector.DynamicVector

abstract class UniformVector2f(
        x: Number, y: Number,
        override val name: String,
        override val qualifier: Qualifier
) : UniformVar<DynamicVector>(), DynamicVector by DynamicVector.create(x.toDouble(), y.toDouble()) {
    override val value: DynamicVector by lazy { this }
    override val type = ShaderType.vec2()
}