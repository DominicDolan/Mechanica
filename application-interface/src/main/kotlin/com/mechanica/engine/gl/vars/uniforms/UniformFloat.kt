package com.mechanica.engine.gl.vars.uniforms

import com.mechanica.engine.gl.qualifiers.Qualifier

abstract class UniformFloat(
        override var value: Float,
        override val name: String,
        override val qualifier: Qualifier
) : UniformVar<Float>() {
    override val type: String = "float"

}