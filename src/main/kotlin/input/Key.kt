package input

open class Key (map: HashMap<Int, ArrayList<Key>>, vararg key: Int) {
    internal var isDown: Boolean = false
        set(value) {
            if (value) hasBeenReleased = true
            else hasBeenPressed = true
            field = value
        }

    private var privateHasBeenDown = false
    var hasBeenReleased: Boolean
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

    private var privateHasBeenUp = true
    var hasBeenPressed: Boolean
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
            if (map.containsKey(it)) {
                map[it]?.add(this)
            } else {
                map[it] = arrayListOf(this)
            }
        }
    }

    constructor(map: HashMap<Int, ArrayList<Key>>, vararg keys: Keys) : this(
            map,
            *(keys.map { it.id }).toIntArray()
            )

}