package com.mechanica.engine.input

import com.mechanica.engine.context.callbacks.MouseHandler
import org.lwjgl.glfw.GLFWScrollCallback

class GLFWScrollHandler(private val handler: MouseHandler): GLFWScrollCallback() {
    override fun invoke(window: Long, xoffset: Double, yoffset: Double) {
        handler.scroll(xoffset, yoffset)
    }
}