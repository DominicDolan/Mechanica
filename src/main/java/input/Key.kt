package input

class Key (map: HashMap<Int, Key>, vararg key: Int) {
    internal var isDown: Boolean = false
        set(value) {
            if (value) hasBeenDown = true
            else hasBeenUp = false
            field = value
        }

    private var privateHasBeenDown = false
    var hasBeenDown: Boolean
        private set(value) {
            privateHasBeenDown = value
        }
        get() {
            if (isDown) {
                return false
            }
            if (privateHasBeenDown) {
                privateHasBeenDown = false
                return true
            }
            return privateHasBeenDown
        }

    private var privateHasBeenUp = false
    var hasBeenUp: Boolean
        private set(value) {
            privateHasBeenUp = value
        }
        get() {
            if (!isDown) {
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

    init {
        key.forEach {
            map[it] = this
        }
    }

}