package com.mechanica.engine.context.loader

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_POLYGON_SMOOTH
import org.lwjgl.opengl.GL11.glDisable
import org.lwjgl.opengl.GL20

class LwjglMiscFactory : MiscFactory {
    override fun prepareStencilForPath() {
        GL20.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE)
        GL20.glStencilFunc(GL20.GL_NOTEQUAL, 1, 0xFF)
        GL20.glStencilMask(0xFF)
    }

    override fun clearStencil() {
        GL20.glClear(GL20.GL_STENCIL_BUFFER_BIT)
    }

    override fun stencilFunction() {
        glDisable(GL_POLYGON_SMOOTH)

        GL20.glStencilFunc(GL20.GL_ALWAYS, 0, 0xFF)
    }

    override fun enableAlphaBlending() {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    }
}