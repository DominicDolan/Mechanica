package com.mechanica.engine.context.loader

import org.lwjgl.opengl.GL11

class LwjglScissorFactory : ScissorFactory {

    override fun enableScissor(x: Int, y: Int, width: Int, height: Int) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        // GL treats a negative extent as an error and draws nothing rather than clamping, so a
        // degenerate rectangle is coerced to an empty one here — which is what the caller meant
        // by it in every case that produces one.
        GL11.glScissor(x, y, maxOf(0, width), maxOf(0, height))
    }

    override fun disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
    }
}
