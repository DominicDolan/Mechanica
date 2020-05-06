package com.mechanica.engine.gl.utils

import com.mechanica.engine.gl.Bindable
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.glBindTexture

inline class Image(val id: Int) : Bindable {
    override fun bind() {
        glBindTexture(GL11.GL_TEXTURE_2D, id)
    }

}