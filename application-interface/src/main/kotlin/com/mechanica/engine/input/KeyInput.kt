package com.mechanica.engine.input

object KeyInput {

    var pressedKeyCount: Int = 0
        private set
    val any: Boolean
        get() = pressedKeyCount > 0
    private val pressedKeys = BooleanArray(1500)

    fun isPressed(keyId: Int) = pressedKeys[keyId]

    internal fun addPressed(key: Int) {
        pressedKeyCount++
        if (key >= 0) {
            pressedKeys[key] = true
        }
    }

    internal fun removePressed(key: Int) {
        if (pressedKeyCount > 0) pressedKeyCount--
        if (key >= 0) {
            pressedKeys[key] = false
        }
    }

}