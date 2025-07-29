package com.mechanica.engine.context.loader

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.glDisable
import org.lwjgl.opengl.GL20

class LwjglMiscFactory : MiscFactory {
    override fun enableAlphaBlending() {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun disableColor() {
        GL20.glColorMask(false, false, false, false)
    }

    override fun enableColor() {
        GL20.glColorMask(true, true, true, true)
    }
}