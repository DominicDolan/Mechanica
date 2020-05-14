package com.mechanica.engine.shader.vars.uniforms

import com.mechanica.engine.shader.qualifiers.Qualifier
import com.mechanica.engine.shader.vars.uniforms.vars.UniformVector3f
import org.lwjgl.opengl.GL20.glUniform3f

class LwjglVector3f(
        x: Number, y: Number, z: Number,
        name: String,
        qualifier: Qualifier
) : UniformVector3f(x, y, z, name, qualifier) {
    override fun loadUniform() {
        glUniform3f(location, value.x, value.y, value.z)
    }
}