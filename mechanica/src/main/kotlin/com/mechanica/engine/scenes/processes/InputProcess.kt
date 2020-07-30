package com.mechanica.engine.scenes.processes

import com.mechanica.engine.input.keyboard.Keyboard
import com.mechanica.engine.input.mouse.Mouse

abstract class InputProcess(order: Int = -1) :  Process(order) {
    val keyboard = Keyboard.create()
    val mouse = Mouse.create()
}