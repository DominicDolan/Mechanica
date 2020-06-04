package com.mechanica.engine.input

import com.mechanica.engine.context.callbacks.MouseHandler
import org.lwjgl.glfw.GLFWCursorPosCallback

class GLFWMouseCursorHandler(private val handler: MouseHandler) : GLFWCursorPosCallback() {
    override fun invoke(window: Long, x: Double, y: Double) {
        handler.cursorMoved(x, y)
    }
}