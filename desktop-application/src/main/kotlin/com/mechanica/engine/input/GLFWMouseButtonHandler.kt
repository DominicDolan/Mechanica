package com.mechanica.engine.input

import com.mechanica.engine.context.callbacks.MouseHandler
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWMouseButtonCallback

class GLFWMouseButtonHandler(private val handler: MouseHandler) : GLFWMouseButtonCallback() {
    override fun invoke(window: Long, button: Int, action: Int, mods: Int) {
        if (action == GLFW.GLFW_PRESS) {
            handler.buttonPressed(button)
        } else if (action == GLFW.GLFW_RELEASE) {
            handler.buttonReleased(button)
        }
    }
}