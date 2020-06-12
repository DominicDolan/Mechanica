package com.mechanica.engine.shader.uniforms

import com.mechanica.engine.shader.qualifiers.Qualifier
import com.mechanica.engine.shader.uniforms.vars.UniformVector2f
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