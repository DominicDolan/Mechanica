package com.mechanica.engine.input

open class Key (label: String, private val condition: () -> Boolean) {
    private val label = label.toUpperCase()

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

    constructor(vararg keys: KeyID) : this(
            keys[0].label,
            keysToBoolean(*keys)
    )

    init {
        hasBeenPressed
        hasBeenReleased
    }

    override fun toString(): String {
        return label
    }

    companion object {

        fun keysToBoolean(vararg key: KeyID): () -> Boolean {
            return {
                var isPressed = false
                for (i in key.indices) {
                    isPressed = KeyInput.isPressed(key[i].id) || isPressed
                }
                isPressed
            }
        }
    }
}