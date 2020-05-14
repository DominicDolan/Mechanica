package com.mechanica.engine.shader.vars.uniforms.vars

import com.mechanica.engine.shader.qualifiers.Qualifier
import com.mechanica.engine.shader.vars.ShaderType

abstract class UniformFloat(
        override var value: Float,
        override val name: String,
        override val qualifier: Qualifier
) : UniformVar<Float>() {
    override val type = ShaderType.float()

}