package input

abstract class ControlsMap {
    private val map = HashMap<Int, ArrayList<Key>>()

    operator fun get(keyId: Int): ArrayList<Key> {
        val keys = map[keyId]
        if (keys != null) {
            return keys
        }
        return arrayListOf(Key(map, keyId))
    }

    inner class ControlsKey(vararg keys: Keys) : Key(map, *keys)
}