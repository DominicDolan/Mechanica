package com.mechanica.engine.input

import com.mechanica.engine.context.callbacks.KeyboardHandler
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWKeyCallback

class GLFWKeyHandler(private val handler: KeyboardHandler) : GLFWKeyCallback() {
    override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        if (action == GLFW.GLFW_PRESS) {
            handler.keyPressed(key)
        } else if (action == GLFW.GLFW_RELEASE) {
            handler.keyReleased(key)
        }
    }
}