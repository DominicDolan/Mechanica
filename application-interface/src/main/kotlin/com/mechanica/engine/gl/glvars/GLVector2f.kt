package com.mechanica.engine.gl.glvars

import com.mechanica.engine.gl.context.GL
import com.mechanica.engine.unit.vector.DynamicVector

class GLVector2f(
        x: Number, y: Number,
        override val name: String,
        override val qualifier: String
) : GLVar<DynamicVector>(), DynamicVector by DynamicVector.create() {
    override val value: DynamicVector by lazy { this }
    override val type: String = "vec2"

    override fun loadUniform() {
        GL.glUniform2f(location, x.toFloat(), y.toFloat())
    }
}