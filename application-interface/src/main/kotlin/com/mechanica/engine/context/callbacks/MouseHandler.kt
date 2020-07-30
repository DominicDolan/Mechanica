package com.mechanica.engine.context.callbacks

import com.mechanica.engine.input.KeyInput

interface MouseHandler {
    fun buttonPressed(key: Int)
    fun buttonReleased(key: Int)
    fun cursorMoved(x: Double, y: Double)
    fun scroll(x: Double, y: Double)

    companion object {
        var cursorX: Double = 0.0
            private set
        var cursorY: Double = 0.0
            private set

        var scrollX: Double = 0.0
            private set
        var scrollY: Double = 0.0
            private set

        internal fun prepare() {
            KeyInput.removePressed(1000)
            KeyInput.removePressed(1001)
            KeyInput.removePressed(1002)
            scrollX = 0.0
            scrollY = 0.0
        }

        internal fun create() = object : MouseHandler {
            override fun buttonPressed(key: Int) {
                KeyInput.addPressed(key)
            }

            override fun buttonReleased(key: Int) {
                KeyInput.removePressed(key)
            }

            override fun cursorMoved(x: Double, y: Double) {
                cursorX = x
                cursorY = y
            }

            override fun scroll(x: Double, y: Double) {
                if (y > 0.0) {
                    KeyInput.addPressed(1000)
                } else if (y < 0.0) {
                    KeyInput.addPressed(1001)
                }
                KeyInput.addPressed(1002)
                scrollX = x
                scrollY = y
            }

        }
    }
}