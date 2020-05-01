package com.mechanica.engine.gl.glvars

import com.mechanica.engine.gl.context.GL

class GLFloat(
        override var value: Float,
        override val name: String,
        override val qualifier: String
) : GLVar<Float>() {
    override val type: String = "float"
    override fun loadUniform() {
        GL.glUniform1f(location, value)
    }
}