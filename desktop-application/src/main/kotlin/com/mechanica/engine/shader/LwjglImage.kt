package com.mechanica.engine.shader

import com.mechanica.engine.models.Image
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.glBindTexture

class LwjglImage(override val id: Int) : Image {
    override fun bind() {
        glBindTexture(GL11.GL_TEXTURE_2D, id)
    }
}