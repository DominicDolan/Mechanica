package com.mechanica.engine.gl.vars.uniforms

import com.mechanica.engine.gl.qualifiers.Qualifier
import org.lwjgl.opengl.GL20.glUniform1f

class LwjglFloat(
        value: Float,
        name: String,
        qualifier: Qualifier
) : UniformFloat(value, name, qualifier) {
    override fun loadUniform() {
        glUniform1f(location, value)
    }
}