package util

abstract class GameBoolean {
    fun update(delta: Double) {
        isTrue = condition()
    }

    abstract fun condition() : Boolean

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