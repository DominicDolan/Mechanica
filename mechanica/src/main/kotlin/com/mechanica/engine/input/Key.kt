package com.mechanica.engine.input

import org.lwjgl.glfw.GLFW

open class Key (private val condition: () -> Boolean) {
    val isDown: Boolean
        get() {
            val isPressed = condition()
            if (isPressed) hasBeenReleased = true
            else hasBeenPressed = true
            return isPressed
        }

    private var privateHasBeenDown = false
    var hasBeenReleased: Boolean
        private set(value) {
            privateHasBeenDown = value
        }
        get() {
            if (isDown) {
                privateHasBeenDown = true
                return false
            }
            if (privateHasBeenDown) {
                privateHasBeenDown = false
                return true
            }
            return privateHasBeenDown
        }

    private var privateHasBeenUp = true
    var hasBeenPressed: Boolean
        private set(value) {
            privateHasBeenUp = value
        }
        get() {
            if (!isDown) {
                privateHasBeenUp = true
                return false
            }
            if (privateHasBeenUp) {
                privateHasBeenUp = false
                return true
            }
            return privateHasBeenUp
        }

    operator fun invoke(): Boolean {
        return isDown
    }

    constructor(vararg key: Int) : this(keysToBoolean(*key))

    constructor(vararg keys: Keys) : this(
            *(keys.map { it.id }).toIntArray()
    )

    companion object {
        fun keysToBoolean(vararg key: Int): () -> Boolean {
            return {
                var isPressed = false
                for (i in key.indices) {
                    isPressed = KeyInput.isPressed(key[i]) || isPressed
                }
                isPressed
            }
        }
    }
}