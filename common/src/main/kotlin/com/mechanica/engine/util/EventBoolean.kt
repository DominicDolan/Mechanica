package com.mechanica.engine.util

open class EventBoolean(private val condition: () -> Boolean) {
    fun update() {
        isTrue = condition()
    }

    fun condition() : Boolean = condition.invoke()

    var isTrue: Boolean = false
        private set(value) {
            if (value) hasBeenFalse = true
            else hasBeenTrue = true
            field = value
        }

    private var privateHasBeenFalse = false
    var hasBeenFalse: Boolean
        private set(value) {
            privateHasBeenFalse = value
        }
        get() {
            if (isTrue) {
                return false
            }
            if (privateHasBeenFalse) {
                privateHasBeenFalse = false
                return true
            }
            return privateHasBeenFalse
        }

    private var privateHasBeenTrue = true
    var hasBeenTrue: Boolean
        private set(value) {
            privateHasBeenTrue = value
        }
        get() {
            if (!isTrue) {
                return false
            }
            if (privateHasBeenTrue) {
                privateHasBeenTrue = false
                return true
            }
            return privateHasBeenTrue
        }

    operator fun invoke(): Boolean {
        return isTrue
    }

}