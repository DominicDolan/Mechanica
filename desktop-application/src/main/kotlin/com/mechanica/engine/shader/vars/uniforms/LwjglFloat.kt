package com.mechanica.engine.shader.vars.uniforms

import com.mechanica.engine.shader.qualifiers.Qualifier
import com.mechanica.engine.shader.vars.uniforms.vars.UniformFloat
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