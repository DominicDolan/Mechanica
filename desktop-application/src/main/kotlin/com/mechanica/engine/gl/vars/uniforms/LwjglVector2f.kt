package com.mechanica.engine.gl.vars.uniforms

import com.mechanica.engine.gl.qualifiers.Qualifier
import org.lwjgl.opengl.GL20.glUniform2f

class LwjglVector2f(
        x: Number, y: Number,
        name: String,
        qualifier: Qualifier
) : UniformVector2f(x, y, name, qualifier) {

    override fun loadUniform() {
        glUniform2f(location, x.toFloat(), y.toFloat())
    }
}