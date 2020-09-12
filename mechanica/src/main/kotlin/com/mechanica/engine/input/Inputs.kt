package com.mechanica.engine.input

import com.mechanica.engine.input.keyboard.Keyboard
import com.mechanica.engine.input.mouse.Mouse

interface Inputs {
    val mouse: Mouse
    val keyboard: Keyboard

    companion object {
        fun create() = object : Inputs {
            override val mouse: Mouse = Mouse.create()
            override val keyboard: Keyboard = Keyboard.create()
        }
    }
}