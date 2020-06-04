package com.mechanica.engine.context.callbacks

import com.mechanica.engine.input.KeyInput
import com.mechanica.engine.input.TextInput

interface KeyboardHandler {
    fun keyPressed(key: Int)
    fun keyReleased(key: Int)
    fun textInput(codepoint: Int)

    companion object {
        internal fun create(): KeyboardHandler = object : KeyboardHandler {
            override fun keyPressed(key: Int) = KeyInput.addPressed(key)
            override fun keyReleased(key: Int) = KeyInput.removePressed(key)
            override fun textInput(codepoint: Int) = TextInput.addCodePoint(codepoint)
        }
    }
}