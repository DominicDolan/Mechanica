package com.mechanica.engine.context.loader

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20

class LwjglStencilFactory : StencilFactory {
    override fun enableStencilWrite(write: Boolean) {
        GL20.glEnable(GL20.GL_STENCIL_TEST)
        if (write) {
            GL20.glStencilMask(0xFF)
        } else {
            GL20.glStencilMask(0x00)
        }
    }

    override fun enableStencilWrite(allowedBits: Int) {
        GL20.glEnable(GL20.GL_STENCIL_TEST)
        GL20.glStencilMask(allowedBits)
    }

    /*
            Stencil test formula:
            (stencil_buffer_value & mask) COMPARE_OP (ref & mask)
         */
    override fun prepareStencil(type: StencilType, ref: Int, mask: Int) {
        GL20.glEnable(GL20.GL_STENCIL_TEST)
        val func = when (type) {
            StencilType.Union -> GL20.GL_ALWAYS
            StencilType.Difference -> GL20.GL_NOTEQUAL
            StencilType.Intersection -> GL20.GL_EQUAL
        }

        GL20.glStencilFunc(func, ref, mask)
        GL20.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE)

    }

    override fun disableStencilTest() {
        GL20.glStencilMask(0x00)
        GL20.glStencilFunc(GL20.GL_ALWAYS, 0, 0xFF)
        GL20.glDisable(GL20.GL_STENCIL_TEST)
    }
}