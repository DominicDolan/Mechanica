package com.mechanica.engine.context.callbacks

import com.mechanica.engine.input.TextInput

interface EventCallbacks {
    val keyboardHandler: KeyboardHandler
    val mouseHandler: MouseHandler

    companion object {
        fun prepare() {
            TextInput.prepare()
            MouseHandler.prepare()
        }

        fun create(): EventCallbacks = object : EventCallbacks {
            override val keyboardHandler = KeyboardHandler.create()
            override val mouseHandler = MouseHandler.create()
        }
    }
}