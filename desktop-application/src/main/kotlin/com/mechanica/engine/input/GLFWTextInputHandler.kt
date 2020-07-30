package com.mechanica.engine.input

import com.mechanica.engine.context.callbacks.KeyboardHandler
import org.lwjgl.glfw.GLFWCharCallback


class GLFWTextInputHandler(private val handler: KeyboardHandler) : GLFWCharCallback() {
    override fun invoke(window: Long, codepoint: Int) {
        handler.textInput(codepoint)
    }
}