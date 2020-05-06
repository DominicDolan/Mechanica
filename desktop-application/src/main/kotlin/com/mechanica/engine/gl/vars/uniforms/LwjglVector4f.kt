package com.mechanica.engine.gl.vars.uniforms

import com.mechanica.engine.gl.qualifiers.Qualifier
import org.lwjgl.opengl.GL20.glUniform4f

class LwjglVector4f (
        x: Number, y: Number, z: Number, w: Number,
        name: String,
        qualifier: Qualifier
) : UniformVector4f(x, y, z, w, name, qualifier) {
    override fun loadUniform() {
        glUniform4f(location, value.x, value.y, value.z, value.w)
    }
}